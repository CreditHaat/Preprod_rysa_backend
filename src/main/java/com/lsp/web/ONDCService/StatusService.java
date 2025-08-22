package com.lsp.web.ONDCService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsp.web.entity.Callback;
import com.lsp.web.repository.CallbackRepository;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import java.util.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import ondc.onboarding.utility.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StatusService {
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private CallbackRepository callbackRepository;
	
	@Autowired
	private TxnLogService txnLogService;
	
	private final RestTemplate restTemplate;
    public StatusService(RestTemplate restTemplate) {
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

	
	public ResponseEntity<?> status(
	        String transactionId,
	        String bppId,
	        String bppUri,
	        String refId,
	        String version
	) {
	    String gatewayUrl = bppUri + "/status";

	    try {
	        String messageId = UUID.randomUUID().toString();
	        String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
	                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

	        // Context
	        Map<String, Object> context = new LinkedHashMap<>();
	        context.put("domain", domain);
	        context.put("location", Map.of(
	            "country", Map.of("code", countryCode),
	            "city", Map.of("code", cityCode)
	        ));
	        context.put("transaction_id", transactionId);
	        context.put("message_id", messageId);
	        context.put("action", "status");
	        context.put("timestamp", timestamp);
	        context.put("version", version);
	        context.put("bap_uri", bapUri);
	        context.put("bap_id", bapId);
	        context.put("ttl", "PT10M");
	        context.put("bpp_id", bppId);
	        context.put("bpp_uri", bppUri);

	        // Message
	        Map<String, Object> message = Map.of("ref_id", refId);

	        Map<String, Object> requestBody = new LinkedHashMap<>();
	        requestBody.put("context", context);
	        requestBody.put("message", message);

	        // Signature
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
	        ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrl, entity, String.class);
	        
	        try {
				String NOresponse = txnLogService.pushTxnLogs("status", entity);
				System.out.println("The Network Observability response we got in status is : "+NOresponse);
			}catch(Exception e) {
				e.printStackTrace();
			}

	        Map<String, Object> responseMap = new LinkedHashMap<>();
	        responseMap.put("transaction_id", transactionId);
	        responseMap.put("message_id", messageId);
	        responseMap.put("gateway_response", objectMapper.readValue(response.getBody(), Object.class));

	        return ResponseEntity.ok(responseMap);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Map.of("error", "Failed to send status request", "details", e.getMessage()));
	    }
	}
	
	//code to store status callback
    public ResponseEntity<?> onStatus(StringBuilder requestBody) throws JsonMappingException, JsonProcessingException{
    	
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
        callback.setApi("/on_status");
//        callback.setProduct("ONDC");
        
     // Broadcast to frontend subscribers
        messagingTemplate.convertAndSend("/topic/callbacks/status/"+transactionId, callback);
        
        callbackRepository.save(callback);
    	
        try {
			String NOresponse = txnLogService.pushTxnLogs("on_status", requestBody.toString());
			System.out.println("The Network Observability response we got in on_status is : "+NOresponse);
		}catch(Exception e) {
			e.printStackTrace();
		}
//    	return null;
//        return ResponseEntity.ok("ACK");
        return ResponseEntity.ok(Map.of("message", Map.of("ack", Map.of("status", "ACK"))));
    	
    }

}
