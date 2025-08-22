package com.lsp.web.ONDCService;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsp.web.Exception.UserInfoNotFoundException;
import com.lsp.web.entity.Apply;
import com.lsp.web.entity.Callback;
import com.lsp.web.entity.JourneyLog;
import com.lsp.web.entity.Logger;
import com.lsp.web.entity.UserInfo;
import com.lsp.web.repository.ApplyRepository;
import com.lsp.web.repository.CallbackRepository;
import com.lsp.web.repository.JourneyLogRepository;
import com.lsp.web.repository.LoggerRepository;
import com.lsp.web.repository.UserInfoRepository;

import ondc.onboarding.utility.Utils;

@Service
public class SelectService {
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private CallbackRepository callbackRepository;
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
	
	private final RestTemplate restTemplate;
	public SelectService(RestTemplate restTemplate) {
	    this.restTemplate = restTemplate;
	}
	 private final ObjectMapper objectMapper = new ObjectMapper();
	    private String bapUri = "https://uat.getrysa.com/";
	    private String bapId = "uat.getrysa.com";
//	    private String version = "2.0.1";
	    private String domain = "ONDC:FIS12";
	    private String countryCode = "IND";
	    private String cityCode = "*";
	    
	    //This variables are needed to create signature-------------------------------
	    private static final String BASE64_PRIVATE_KEY = "MnP2sPvCnMlFVR3k/PfORhIATn+V9bVNd3EgNHVRA18=";
	    private static final String SUBSCRIBER_ID = "uat.getrysa.com";
	    private static final String UNIQUE_KEY_ID = "d744ee65-0831-463f-b489-7add070f96f4";

	    //here we need product name for saving the data in applyrecord with productname
	public ResponseEntity<?> select(String transactionId, String bppId, String bppUri, String providerId, String itemId,
			String formId, String submissionId, String status, String mobileNumber, Integer stage, String productName, String loanAmount, String version){
	
		System.out.println("The status is :: "+status);
		
		String gatewayUrl = bppUri+"/select";
		
		try {
			
			
			
            String messageId = UUID.randomUUID().toString();
//            String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
            String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

            // ---------- Context ----------
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("domain", domain);
            context.put("location", Map.of(
                    "country", Map.of("code", countryCode),
                    "city", Map.of("code", cityCode)
            ));
            context.put("transaction_id", transactionId);
            context.put("message_id", messageId);
            context.put("action", "select");
            context.put("timestamp", timestamp);
            context.put("version", version);
            context.put("bap_uri", bapUri);
            context.put("bap_id", bapId);
            context.put("ttl", "PT10M");
            context.put("bpp_id", bppId);
            context.put("bpp_uri", bppUri);

            // ---------- Message ----------
            Map<String, Object> message = Map.of(
                    "order", Map.of(
                            "provider", Map.of("id", providerId),
                            "items", List.of(
                                    Map.of(
                                            "id", itemId,
                                            "xinput", Map.of(
                                                    "form", Map.of("id", formId),
                                                    "form_response", Map.of(
                                                            "status", status,
                                                            "submission_id", submissionId
                                                    )
                                            )
                                    )
                            )
                    )
            );

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("context", context);
            requestBody.put("message", message);
            
            //code to create authorization signature
            long created = System.currentTimeMillis() / 1000L;
            long expires = created + 300000;
//            logger.info(toBase64(generateBlakeHash(req.get("value").toString())));
//            logger.info(req.get("value").toString());
            String hashedReq = Utils.hashMassage(objectMapper.writeValueAsString(requestBody),created,expires);
            String signature = Utils.sign(Base64.getDecoder().decode(BASE64_PRIVATE_KEY),hashedReq.getBytes());
            String subscriberId = SUBSCRIBER_ID;
            String uniqueKeyId = UNIQUE_KEY_ID;

            String authorizationSignature = "Signature keyId=\"" + subscriberId + "|" + uniqueKeyId + "|" + "ed25519\"" + ",algorithm=\"ed25519\"," + "created=\"" + created + "\",expires=\"" + expires + "\",headers=\"(created) (expires)" + " digest\",signature=\"" + signature + "\"";
        


            // ---------- HTTP Request ----------
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", authorizationSignature);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            //Code not related to api just for db storage-----------------------------
            //Load the userInfo to save the userInfo id into journeyLog
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
//              logger.setUrl(gatewayUrl);// this url doesnt refers the value of api url it holds the url if we get from response of that api
              logger.setRequestPayload(String.valueOf(entity));
//              logger.setResponsePayload(String.valueOf(responseMap));
              loggerRepository.save(logger);
              //------------------------------------------------------------------------


            ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrl, entity, String.class);
            
            try {
				String NOresponse = txnLogService.pushTxnLogs("select", entity);
				System.out.println("The Network Observability response we got in select is : "+NOresponse);
			}catch(Exception e) {
				e.printStackTrace();
			}

            // ---------- Response Map ----------
            Map<String, Object> responseMap = new LinkedHashMap<>();
            responseMap.put("transaction_id", transactionId);
            responseMap.put("message_id", messageId);
            responseMap.put("gateway_response", objectMapper.readValue(response.getBody(), Object.class));
            
            logger.setResponsePayload(String.valueOf(responseMap));
            loggerRepository.save(logger);
            
            //here we will be writing the apply record if the stage is 2
            if(stage == 2) {
            	
//            	update apply table
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
    			
    			//here we will update our userInfo table by saving the loanAmount into it
    			userInfo.setLoanAmount(Float.parseFloat(loanAmount));
    			userInfoRepository.save(userInfo);    			
            }
            
            
           
            return ResponseEntity.ok(responseMap);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send select", "details", e.getMessage()));
        }
    }
	
	//code to store select callback
    public ResponseEntity<?> onSelect(StringBuilder requestBody) throws JsonMappingException, JsonProcessingException{
    	
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
        callback.setApi("/on_select");
//        callback.setProduct("ONDC");
        
     // Broadcast to frontend subscribers
        messagingTemplate.convertAndSend("/topic/callbacks/select/"+transactionId, callback);
        
        callbackRepository.save(callback);
        
        try {
			String NOresponse = txnLogService.pushTxnLogs("on_select", requestBody.toString());
			System.out.println("The Network Observability response we got in on_select is : "+NOresponse);
		}catch(Exception e) {
			e.printStackTrace();
		}
    	
    	
//    	return null;
//        return ResponseEntity.ok("ACK");
        return ResponseEntity.ok(Map.of("message", Map.of("ack", Map.of("status", "ACK"))));
    	
    }

}


