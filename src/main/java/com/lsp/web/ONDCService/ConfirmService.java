package com.lsp.web.ONDCService;

import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsp.web.Exception.UserInfoNotFoundException;
import com.lsp.web.entity.Apply;
import com.lsp.web.entity.Callback;
import com.lsp.web.entity.JourneyLog;
import com.lsp.web.entity.Logger;
import com.lsp.web.entity.Repayment;
import com.lsp.web.entity.UserInfo;
import com.lsp.web.repository.ApplyRepository;
import com.lsp.web.repository.CallbackRepository;
import com.lsp.web.repository.JourneyLogRepository;
import com.lsp.web.repository.LoggerRepository;
import com.lsp.web.repository.RepaymentRepository;
import com.lsp.web.repository.UserInfoRepository;

import ondc.onboarding.utility.Utils;

@Service
public class ConfirmService {
	
	@Autowired
	private RepaymentRepository repaymentRepository;
	
	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private LoggerRepository loggerRepository;
	@Autowired
	private JourneyLogRepository journeyLogRepository;
	@Autowired
	private UserInfoRepository userInfoRepository;
	@Autowired
	private ApplyRepository applyRepository;
	
	@Autowired
	private TxnLogService txnLogService;
	
	@Autowired
	private DisbursedLoanService disbursedLoanService;

	private final RestTemplate restTemplate;

	public ConfirmService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

	private final ObjectMapper objectMapper = new ObjectMapper();
    private String bapUri = "https://uat.getrysa.com/";
    private String bapId = "uat.getrysa.com";
//    private String version = "2.0.1";
    private String domain = "ONDC:FIS12";
    private String countryCode = "IND";
    private String cityCode = "*";
    
    //This variables are needed to create signature-------------------------------
    private static final String BASE64_PRIVATE_KEY = "MnP2sPvCnMlFVR3k/PfORhIATn+V9bVNd3EgNHVRA18=";
    private static final String SUBSCRIBER_ID = "uat.getrysa.com";
    private static final String UNIQUE_KEY_ID = "d744ee65-0831-463f-b489-7add070f96f4";
	
	public ResponseEntity<?> confirm(
	        String transactionId,
	        String bppId,
	        String bppUri,
	        String providerId,
	        String itemId,
	        String formId,
	        String submissionId,
	        String bankCode,
	        String accountNumber,
	        String vpa,
	        String settlementAmount,
	        String version,
	        String paymentId,
	        String mobileNumber, Integer stage, String productName
	) {
	    String gatewayUrl = bppUri + "/confirm";

	    try {
	        String messageId = UUID.randomUUID().toString();
//	        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
	        String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

	        // Context block
	        Map<String, Object> context = new LinkedHashMap<>();
	        context.put("domain", domain);
	        context.put("location", Map.of(
	            "country", Map.of("code", countryCode),
	            "city", Map.of("code", cityCode)
	        ));
	        context.put("transaction_id", transactionId);
	        context.put("message_id", messageId);
	        context.put("action", "confirm");
	        context.put("timestamp", timestamp);
	        context.put("version", version);
	        context.put("bap_uri", bapUri);
	        context.put("bap_id", bapId);
	        context.put("ttl", "PT10M");
	        context.put("bpp_id", bppId);
	        context.put("bpp_uri", bppUri);

	        // Payment tag blocks
	        Map<String, Object> buyerFinderFees = Map.of(
	            "descriptor", Map.of("code", "BUYER_FINDER_FEES"),
	            "display", false,
	            "list", List.of(
	                Map.of("descriptor", Map.of("code", "BUYER_FINDER_FEES_TYPE"), "value", "percent-annualized"),
	                Map.of("descriptor", Map.of("code", "BUYER_FINDER_FEES_PERCENTAGE"), "value", "1")
	            )
	        );

	        Map<String, Object> settlementTerms = Map.of(
	            "descriptor", Map.of("code", "SETTLEMENT_TERMS"),
	            "display", false,
	            "list", List.of(
	                Map.of("descriptor", Map.of("code", "SETTLEMENT_AMOUNT"), "value", settlementAmount),
	                Map.of("descriptor", Map.of("code", "SETTLEMENT_TYPE"), "value", "neft"),
	                Map.of("descriptor", Map.of("code", "DELAY_INTEREST"), "value", "5"),
	                Map.of("descriptor", Map.of("code", "STATIC_TERMS"), "value", "https://bap.credit.becknprotocol.io/personal-banking/loans/personal-loan"),
	                Map.of("descriptor", Map.of("code", "OFFLINE_CONTRACT"), "value", "true")
	            )
	        );

	        // Message block
	        Map<String, Object> message = Map.of(
	            "order", Map.of(
	                "provider", Map.of("id", providerId),
	                "items", List.of(
	                    Map.of(
	                        "id", itemId,
	                        "xinput", Map.of(
	                            "form", Map.of("id", formId),
	                            "form_response", Map.of(
	                                "status", "SUCCESS",
	                                "submission_id", submissionId
	                            )
	                        )
	                    )
	                ),
	                "payments", List.of(
	                    Map.of(
	                        "id", paymentId,
	                        "collected_by", "BPP",
	                        "type", "ON_ORDER",
	                        "status", "NOT-PAID",
	                        "params", Map.of(
	                            "bank_code", bankCode,
	                            "bank_account_number", accountNumber,
	                            "virtual_payment_address", vpa
	                        ),
	                        "tags", List.of(buyerFinderFees, settlementTerms)
	                    )
	                )
	            )
	        );

	        Map<String, Object> requestBody = Map.of(
	            "context", context,
	            "message", message
	        );

	        // Sign and send request
	        long created = System.currentTimeMillis() / 1000L;
	        long expires = created + 300;
	        String hashedReq = Utils.hashMassage(objectMapper.writeValueAsString(requestBody), created, expires);
	        String signature = Utils.sign(Base64.getDecoder().decode(BASE64_PRIVATE_KEY), hashedReq.getBytes());

	        String authorizationHeader = "Signature keyId=\"" + SUBSCRIBER_ID + "|" + UNIQUE_KEY_ID + "|ed25519\"," +
	            "algorithm=\"ed25519\"," +
	            "created=\"" + created + "\"," +
	            "expires=\"" + expires + "\"," +
	            "headers=\"(created) (expires) digest\"," +
	            "signature=\"" + signature + "\"";

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
	        headers.set("Authorization", authorizationHeader);

	        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
	        
	        //saving the logs before calling api -----------------------------------------------------------
	        
	      //Before calling the api we will be saving the logs---------------------------
            UserInfo userInfo;
            Optional<UserInfo> optionalUserInfo = userInfoRepository.findByMobileNumber(mobileNumber);
            if(optionalUserInfo.isEmpty()) {
            	throw new UserInfoNotFoundException("UserInfo not found with mobileNumber : "+mobileNumber);
            	
            }
            userInfo = optionalUserInfo.get();

            //logic to save the journey log
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setPlatformId("O");
            journeyLog.setRequestId(gatewayUrl);
            journeyLog.setStage(stage);
            journeyLog.setUId(transactionId);
            journeyLog.setUser(userInfo);
            journeyLogRepository.save(journeyLog);
            
            //here we will save this api call in logger
            Logger logger = new Logger();
            logger.setJourneyLog(journeyLog);
//            logger.setUrl(gatewayUrl);// this url doesnt refers the value of api url it holds the url if we get from response of that api
            logger.setRequestPayload(String.valueOf(entity));
//            logger.setResponsePayload(String.valueOf(responseMap));
            loggerRepository.save(logger);
            //----------------------------------------------------------------------------
	        
	        //----------------------------------------------------------------------------------------------

	        ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrl, entity, String.class);
	        
	        try {
				String NOresponse = txnLogService.pushTxnLogs("confirm", entity);
				System.out.println("The Network Observability response we got in confirm is : "+NOresponse);
			}catch(Exception e) {
				e.printStackTrace();
			}

	        Map<String, Object> responseMap = new LinkedHashMap<>();
	        responseMap.put("transaction_id", transactionId);
	        responseMap.put("message_id", messageId);
	        responseMap.put("gateway_response", objectMapper.readValue(response.getBody(), Object.class));
	        
	        logger.setResponsePayload(String.valueOf(responseMap));
            loggerRepository.save(logger);
            
            Apply apply = null ;
        	Optional<Apply> optionalApply = applyRepository.findByUserAndProductName(userInfo, productName);
        	if(optionalApply.isEmpty()) {
        		apply = new Apply();
        		apply.setMobileNumber(mobileNumber);
    			apply.setProductName(productName);
        	}else if(optionalApply.isPresent()) { 
        		apply = optionalApply.get();
        	}
			apply.setStage(stage);//we will be fetching this stage from the journeyLog table and then we will be updating this stage here
			apply.setUser(userInfo);
			
			applyRepository.save(apply);

	        return ResponseEntity.ok(responseMap);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Failed to send confirm", "details", e.getMessage()));
	    }
	}
	
	//on_confirm callback service
    public ResponseEntity<?> onConfirm(StringBuilder requestBody) throws JsonMappingException, JsonProcessingException{
    	
    	// Parse JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody.toString());

        String transactionId = jsonNode.path("context").path("transaction_id").asText();
        String messageId = jsonNode.path("context").path("message_id").asText();
 
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Message ID: " + messageId);
        
        Callback callback = new Callback();
        callback.setuID(transactionId);
        callback.setApiId(messageId);
        callback.setContent(requestBody.toString());
        callback.setApi("/on_confirm");
//        callback.setProduct("ONDC");
        
        callbackRepository.save(callback);
        
     // Broadcast to frontend subscribers
        messagingTemplate.convertAndSend("/topic/callbacks/confirm/"+transactionId, callback);
        
        try {
			String NOresponse = txnLogService.pushTxnLogs("on_confirm", requestBody.toString());
			System.out.println("The Network Observability response we got in on_confirm is : "+NOresponse);
		}catch(Exception e) {
			e.printStackTrace();
		}
        
        //handles both 2.0.0 and 2.0.1 version
        disbursedLoanService.saveLoanFromOnConfirm(String.valueOf(requestBody));
        
     // Extract EMI Amount from LOAN_INFO tag
        String emiAmount = null;
        JsonNode itemsNode = jsonNode.path("message").path("order").path("items");
        if (itemsNode.isArray() && itemsNode.size() > 0) {
            for (JsonNode tag : itemsNode.get(0).path("tags")) {
                if ("LOAN_INFO".equalsIgnoreCase(tag.path("descriptor").path("code").asText())) {
                    for (JsonNode listItem : tag.path("list")) {
                        if ("INSTALLMENT_AMOUNT".equalsIgnoreCase(listItem.path("descriptor").path("code").asText())) {
                            emiAmount = listItem.path("value").asText();
                        }
                    }
                }
            }
        }

        // Loop through POST_FULFILLMENT payments
        JsonNode paymentsNode = jsonNode.path("message").path("order").path("payments");
        if (paymentsNode.isArray()) {
            for (JsonNode payment : paymentsNode) {
                if ("POST_FULFILLMENT".equalsIgnoreCase(payment.path("type").asText())) {

                    String paymentId = payment.path("id").asText();
                    if (!repaymentRepository.existsByTransactionIdAndPaymentId(transactionId, paymentId)) {
                        Repayment repayment = new Repayment();
                        repayment.setTransactionId(transactionId);
                        repayment.setPaymentId(paymentId);
                        repayment.setEmiAmount(emiAmount);

                        repayment.setAmountToPay(payment.path("params").path("amount").asText());
                        repayment.setRemainingAmount(payment.path("params").path("amount").asText());

                        String dueDateStr = payment.path("time").path("range").path("end").asText();
                        if (dueDateStr != null && !dueDateStr.isEmpty()) {
                            LocalDateTime dueDate = LocalDateTime.parse(dueDateStr, DateTimeFormatter.ISO_DATE_TIME);
                            repayment.setDueDate(dueDate);
                        }

                        repaymentRepository.save(repayment);
                    }
                }
            }
        }
    	
//    	return null;
        return ResponseEntity.ok(Map.of("message", Map.of("ack", Map.of("status", "ACK"))));
    	
    }


}
