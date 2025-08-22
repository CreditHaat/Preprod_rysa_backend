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
import java.util.List;
import java.util.Map;
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
import com.lsp.web.Exception.LoanNotFoundException;
import com.lsp.web.ONDCService.ConfirmService;
import com.lsp.web.ONDCService.DisbursedLoanService;
import com.lsp.web.ONDCService.InitService;
import com.lsp.web.ONDCService.LoanInstallmentsService;
import com.lsp.web.ONDCService.SearchService;
import com.lsp.web.ONDCService.SelectService;
import com.lsp.web.ONDCService.StatusService;
import com.lsp.web.ONDCService.UpdateService;
import com.lsp.web.dto.ConfirmRequestDTO;
import com.lsp.web.dto.DisbursedLoanDTO2;
import com.lsp.web.dto.ForeclosureUpdateRequestDTO;
import com.lsp.web.dto.InitRequestDTO;
import com.lsp.web.dto.MissedPaymentUpdateRequestDTO;
import com.lsp.web.dto.SearchRequestDTO;
import com.lsp.web.dto.SelectRequestDTO;
import com.lsp.web.dto.StatusRequestDTO;
import com.lsp.web.dto.UpdateFulfillmentRequestDTO;
import com.lsp.web.dto.UpdatePaymentRequestDTO;
import com.lsp.web.dto.UpdateRequestDTO;

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
	
//	@PostMapping("/networkobservability")
//	public ResponseEntity<?> networkObservability(@RequestBody Map<> )

}