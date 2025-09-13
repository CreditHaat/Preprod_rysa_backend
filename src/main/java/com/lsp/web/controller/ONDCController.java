package com.lsp.web.controller;

//import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsp.web.Exception.LoanNotFoundException;
import com.lsp.web.ONDCService.ConfirmService;
import com.lsp.web.ONDCService.DisbursedLoanService;
import com.lsp.web.ONDCService.FinvuService;
import com.lsp.web.ONDCService.InitService;
import com.lsp.web.ONDCService.LoanInstallmentsService;
import com.lsp.web.ONDCService.SearchService;
import com.lsp.web.ONDCService.SelectService;
import com.lsp.web.ONDCService.StatusService;
import com.lsp.web.ONDCService.UpdateService;
import com.lsp.web.dto.ConfirmRequestDTO;
import com.lsp.web.dto.DisbursedLoanDTO2;
import com.lsp.web.dto.ForeclosureUpdateRequestDTO;
import com.lsp.web.dto.FormLogRequest;
import com.lsp.web.dto.InitRequestDTO;
import com.lsp.web.dto.MissedPaymentUpdateRequestDTO;
import com.lsp.web.dto.ONDCFormDataDTO;
import com.lsp.web.dto.SearchRequestDTO;
import com.lsp.web.dto.SelectRequestDTO;
import com.lsp.web.dto.StatusRequestDTO;
import com.lsp.web.dto.UpdateFulfillmentRequestDTO;
import com.lsp.web.dto.UpdatePaymentRequestDTO;
import com.lsp.web.dto.UpdateRequestDTO;
import com.lsp.web.entity.Callback;
import com.lsp.web.repository.CallbackRepository;

import ondc.onboarding.utility.Routes;
import ondc.onboarding.utility.Utils;
///////////////////////

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin("*")
public class ONDCController extends Utils {

	@Autowired
	private SearchService searchService;
	@Autowired
	private SelectService selectService;
	@Autowired
	private InitService initService;
	@Autowired
	private ConfirmService confirmService;
	@Autowired
	private UpdateService updateService;
	@Autowired
	private StatusService statusService;

	@Autowired
	private DisbursedLoanService loanService; // Service where saveLoanFromOnConfirm is defined

	@Autowired
	private LoanInstallmentsService loanInstallmentService;
	
	@Autowired
	private FinvuService finvuService;
	
	@Autowired
	private CallbackRepository callbackRepository;

	@GetMapping("/createTransactionId")
	public ResponseEntity<?> createId() {
		try {
			String transactionId = UUID.randomUUID().toString();
			return ResponseEntity.ok(transactionId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Failed to create transactionId", "details", e.getMessage()));
		}
	}

	// here we are taking the mobileNumber just to get UserInfo and save it in
	// journeyLog its not related to ONDC's search api
	@PostMapping("/search")
//	@RequestBody
	public ResponseEntity<?> triggerSearch(@RequestBody SearchRequestDTO searchRequestDTO) {
		return searchService.search(searchRequestDTO.getTransactionId(), searchRequestDTO.getMobileNumber(),
				searchRequestDTO.getStage());
	}

	@PostMapping("/select")
	public ResponseEntity<?> triggerSelect(@RequestBody SelectRequestDTO selectRequestDTO) {
		return selectService.select(selectRequestDTO.getTransactionId(), selectRequestDTO.getBppId(),
				selectRequestDTO.getBppUri(), selectRequestDTO.getProviderId(), selectRequestDTO.getItemId(),
				selectRequestDTO.getFormId(), selectRequestDTO.getSubmissionId(), selectRequestDTO.getStatus(),
				selectRequestDTO.getMobileNumber(), selectRequestDTO.getStage(), selectRequestDTO.getProductName(),
				selectRequestDTO.getLoanAmount(), selectRequestDTO.getVersion());
	}

	@PostMapping("/init")
	public ResponseEntity<?> callInit(@RequestBody InitRequestDTO request) {
		return initService.init(request.getTransactionId(), request.getBppId(), request.getBppUri(),
				request.getProviderId(), request.getItemId(), request.getFormId(), request.getSubmissionId(),
				request.getBankCode(), request.getAccountNumber(), request.getVpa(), request.getSettlementAmount(),
				request.getMobileNumber(), request.getStage(), request.getProductName(), request.getFormType(),
				request.getAccountname(), request.getAccountType(), request.getIFSC(), request.getVersion(),
				request.getInitAttempt(), request.getPaymentId());
	}

	@PostMapping("/confirm")
	public ResponseEntity<?> selectRequest(@RequestBody ConfirmRequestDTO request) {// to change this to
																					// ConfirmRequestDTO
		return confirmService.confirm(request.getTransactionId(), request.getBppId(), request.getBppUri(),
				request.getProviderId(), request.getItemId(), request.getFormId(), request.getSubmissionId(),
				request.getBankCode(), request.getAccountNumber(), request.getVpa(), request.getSettlementAmount(),
				request.getVersion(), request.getPaymentId(), request.getMobileNumber(), request.getStage(),
				request.getProductName());
	}

	@PostMapping("/update")
	public ResponseEntity<?> updateRequest(@RequestBody UpdateRequestDTO request) {
		return updateService.update(request.getTransactionId(), request.getBppId(), request.getBppUri(),
				request.getOrderId(), request.getFulfillmentState(), request.getVersion());
	}

	//This is to update the part payment
	@PostMapping("/update-payment")
	public ResponseEntity<?> updatePaymentRequest(@RequestBody UpdatePaymentRequestDTO request) {
		return updateService.updatePayment(request.getTransactionId(), request.getBppId(), request.getBppUri(),
				request.getOrderId(), request.getAmount(), request.getCurrency(), request.getVersion());
	}

	@PostMapping("/update-fulfillment")
	public ResponseEntity<?> updateFulfillmentRequest(@RequestBody UpdateFulfillmentRequestDTO request) {
		return updateService.updateFulfillment(request.getTransactionId(), request.getBppId(), request.getBppUri(),
				request.getOrderId(), request.getFulfillmentState(), request.getVersion());
	}

	@PostMapping("/status")
	public ResponseEntity<?> statusRequest(@RequestBody StatusRequestDTO request) {
		return statusService.status(request.getTransactionId(), request.getBppId(), request.getBppUri(),
				request.getRefId(), request.getVersion());
	}

	@PostMapping("/update-missed-emi")
	public ResponseEntity<?> updateMissedPayment(@RequestBody MissedPaymentUpdateRequestDTO request) {
		return updateService.updateMissedPayment(request.getTransactionId(), request.getBppId(), request.getBppUri(),
				request.getOrderId(), request.getPaymentLabel(), request.getVersion());
	}

	@PostMapping("/update-foreclosure")
	public ResponseEntity<?> updateForeclosure(@RequestBody ForeclosureUpdateRequestDTO request) {
		return updateService.updateForeclosurePayment(request.getTransactionId(), request.getBppId(),
				request.getBppUri(), request.getOrderId(), request.getPaymentLabel(), request.getVersion());
	}

//	 @PostMapping("/fetchPrepartPayment")
//	 public 

	@PostMapping("/save-loan")
	public ResponseEntity<?> testSaveLoan(@RequestBody String requestBody) {
		try {
			loanService.saveLoanFromOnConfirm(requestBody);
			return ResponseEntity.ok(Map.of("status", "success", "message", "Loan data saved from on_confirm payload"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", e.getMessage()));
		}
	}

//	    @PostMapping("/fetchloans")
//	    public ResponseEntity<?> fetchLoans(@RequestBody String mobileNumber){
//	    	try {
//	    		return loanService.fetchLoans(mobileNumber);
//	    	}catch(Exception e) {
//	    		e.printStackTrace();
//	    	}
//	    	return null;
//	    }
//	@PostMapping("/fetchloans")
//	public ResponseEntity<?> fetchLoans(@RequestBody Map<String, String> request) {
//		String mobileNumber = request.get("mobileNumber");
//		return loanService.fetchLoans(mobileNumber);
//	}
	@PostMapping("/fetchloans")
	public ResponseEntity<?> fetchLoans(@RequestBody Map<String, String> request) {
	    String mobileNumber = request.get("mobileNumber");
	    return loanService.fetchLoans(mobileNumber);
	   
	}

	@PostMapping("/fetchInstallmentByEndDate")
	public ResponseEntity<?> fetchInstallmentByEndDate(@RequestBody Map<String, String> request) {
		try {
			String loanNumber = request.get("loanNumber");
			return ResponseEntity.ok(loanInstallmentService.getUpcomingInstallment(loanNumber));
		}catch(LoanNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", e.getMessage()));
		}
		
	}
	
	@PostMapping("/save-prepartpayment")
	public ResponseEntity<?> savePartPayment(@RequestBody String requestBody) {
		try {
			loanService.updateLoanFromOnUpdate(requestBody);
			return ResponseEntity.ok(Map.of("status", "success", "message", "Loan data saved from on_confirm payload"));
		}
		catch(LoanNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", e.getMessage()));
		}
	}
	
//	@PostMapping("/fetchInstallmentsByLoan")
//	public ResponseEntity<?> fetchInstallmentsByLoan(@RequestParam(name="loanId") String loanId) {
//		try {
////			String loanNumber = request.get("loanNumber");
//			return ResponseEntity.ok(loanInstallmentService.fetchInstallmentsByLoan(loanId));
//		}catch(LoanNotFoundException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//		}
//		catch(Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(Map.of("status", "error", "message", e.getMessage()));
//		}
//		
//	}
	
	@PostMapping("/fetchInstallmentsByLoan")
	public ResponseEntity<?> fetchInstallmentsByLoan(@RequestBody Map<String, String> request) {
	    try {
	        String loanId = request.get("loanId");  // fetch from JSON body
	        return ResponseEntity.ok(loanInstallmentService.fetchInstallmentsByLoan(loanId));
	    } catch (LoanNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("status", "error", "message", e.getMessage()));
	    }
	}

	@PostMapping("/fetchMissedEmiByLoan")
	public ResponseEntity<?> fetchMissedEmiByLoan(@RequestBody Map<String, String> request) {
	    try {
	        String loanId = request.get("loanId");  // fetch from JSON body
	        return ResponseEntity.ok(loanInstallmentService.fetchMissedEmiByLoan(loanId));
	    } catch (LoanNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("status", "error", "message", e.getMessage()));
	    }
	}
	
//	@PostMapping("/writeFormLogs")
//	public ResponseEntity<?> writeFormLogs(@RequestParam String mobileNumber, @RequestParam String transactionId, @RequestParam String request, @RequestParam String response, @RequestParam String gatewayUrl ){
//		try {
//			searchService.writeFormLogs(mobileNumber, transactionId,request, response, gatewayUrl);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
//	@PostMapping("/writeFormLogs")
//	public ResponseEntity<?> writeFormLogs(@RequestBody FormLogRequest formLogRequest) {
//	    try {
//	        searchService.writeFormLogs(
//	            formLogRequest.getMobileNumber(),
//	            formLogRequest.getTransactionId(),
//	            formLogRequest.getRequest().toString(), // keep as JSON string if storing
//	            formLogRequest.getResponse(),
//	            formLogRequest.getGatewayUrl()
//	        );
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	    return ResponseEntity.ok("Form logs saved");
//	}
	
	@PostMapping("WriteFormLogs")
	public ResponseEntity<?> writeFormLogs(@RequestBody FormLogRequest formLogRequest){
		try {
			
			String mobilenumber = formLogRequest.getOndcFormDataDTO().getContactNumber();
			String ondcFormDTOJSON = null;
			if(formLogRequest.getOndcFormDataDTO()!=null) {
				ObjectMapper mapper = new ObjectMapper(); // ✅ create instance
	            ondcFormDTOJSON = mapper.writeValueAsString(formLogRequest.getOndcFormDataDTO());
			}
			String responseJSON = null;
			if(formLogRequest.getResponse()!=null) {
				ObjectMapper mapper = new ObjectMapper();
				responseJSON =mapper.writeValueAsString(formLogRequest.getResponse());
			}
			
			String gatewayUrl = formLogRequest.getGatewayUrl();
			String transactionId = formLogRequest.getTransactionId();
			String formSubmissionStatus = formLogRequest.getFormSubmissionStatus();
			String productName = formLogRequest.getProductName();
			searchService.writeFormLogs(mobilenumber, ondcFormDTOJSON, responseJSON, gatewayUrl, transactionId, formSubmissionStatus, productName);
			return ResponseEntity.ok("DONE"); 
		}catch(Exception e) {
			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(Map.of("status", "error", "message", e.getMessage()));
		}
	}
//	@PostMapping("/networkobservability")
//	public ResponseEntity<?> networkObservability(@RequestBody Map<> )
	
	@PostMapping("/finvuRedirect")
	public ResponseEntity<?> finvuRedirect(@RequestParam(name="transactionId") String txnid,@RequestParam(name="srcref") String srcref, @RequestParam(name="mobileNumber") String mobileNumber, @RequestParam(name="redirectUrl") String redirecturl){
		try {
			String redirectionlinkforaa = finvuService.linkparam(txnid, srcref, mobileNumber, redirecturl);
			return ResponseEntity.ok(redirectionlinkforaa);
		}catch(Exception e) {
			return null;
		}
	}
	
	@PostMapping("/setFrontendContext")
	public ResponseEntity<?> frontendContext(@RequestParam(name="transactionId") String transactionId, @RequestParam("bppId") String bppId){
		try {
//			List<Callback> callbacks = callbackRepository.findByuID(transactionId);
//			Optional<Callback> callbacks = callbackRepository.findByuIdLatest(transactionId);
//			"pahal.lenderbridge.uat.ignosis.ai"
			Optional<Callback> callbacks = callbackRepository.findByTransactionIdAndBppId(transactionId, bppId);
			if(callbacks.isEmpty())
			{
				return null;
			}else {
				if(callbacks.get().getApi().equalsIgnoreCase("/on_status")) {
					
					//here we will get the submission id from the on_status
					//code to get submission id
					
					//here we will find again from db the record before on_status
					Optional<Callback> callbacks2 = callbackRepository.findLatestExcludingAction(transactionId, bppId, "on_status");//here on_status is the action that we will be skipping
					if(callbacks2.isEmpty()) {
						return null;
					}else {
						Callback returnCallback = callbacks2.get();
						Map<String,Object> map = new HashMap();
						map.put("content1", callbacks2);
						map.put("content2", callbacks);
						return ResponseEntity.ok(map);
					}
				}
			}
			
			return ResponseEntity.ok(callbacks);
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
//	@PostMapping("/getLatestCallbacks")
//	public ResponseEntity<?> latestCallback(@RequestParam(name="transactionId") String transactionId){
//		try {
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

}