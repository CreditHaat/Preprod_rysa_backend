package com.lsp.web.ONDCService;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsp.web.Exception.UserInfoNotFoundException;
import com.lsp.web.dto.ONDCFormDataDTO;
import com.lsp.web.entity.ApplyFail;
import com.lsp.web.entity.Callback;
import com.lsp.web.entity.JourneyLog;
import com.lsp.web.entity.Logger;
import com.lsp.web.entity.UserInfo;
import com.lsp.web.repository.ApplyFailRepository;
import com.lsp.web.repository.CallbackRepository;
import com.lsp.web.repository.JourneyLogRepository;
import com.lsp.web.repository.LoggerRepository;
import com.lsp.web.repository.UserInfoRepository;

import ondc.onboarding.utility.Utils;

//import com.lsp.web.controller.ObjectMapper;
//import com.lsp.web.controller.RestTemplate;

@Service
public class SearchService {

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
	private TxnLogService txnLogService;

	private final RestTemplate restTemplate;
	
	@Autowired
	private ApplyFailRepository applyFailRepository;

	public SearchService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private final ObjectMapper objectMapper = new ObjectMapper();
	private String bapUri = "https://uat.getrysa.com/";
	private String bapId = "uat.getrysa.com";
	private String version = "2.0.1";
	private String domain = "ONDC:FIS12";
	private String countryCode = "IND";
	private String cityCode = "*";
	private String gatewayUrl = "https://preprod.gateway.proteantech.in/search";

	// This variables are needed to create signature-------------------------------
	private static final String BASE64_PRIVATE_KEY = "MnP2sPvCnMlFVR3k/PfORhIATn+V9bVNd3EgNHVRA18=";
	private static final String SUBSCRIBER_ID = "uat.getrysa.com";
	private static final String UNIQUE_KEY_ID = "d744ee65-0831-463f-b489-7add070f96f4";

//    private static final String CREATED_ISO = "2025-06-19T11:12:50.788Z";
//    private static final String EXPIRES_ISO = "2026-10-20T18:00:15.071Z";
	// -----------------------------------------------------------------------------

	// we will be taking the mobileNumber in this so that we can load and save the
	// UserInfo into journeyLog of the user who is doing this journey
	public ResponseEntity<?> search(String transactionId, String mobileNumber, Integer stage) {
		try {

			System.out.println("The transaction id that we got in search : " + transactionId);

//            String transactionId = UUID.randomUUID().toString();
			String messageId = UUID.randomUUID().toString();
//            String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
			String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

			Map<String, Object> requestBody = new LinkedHashMap<>();

			Map<String, Object> context = new LinkedHashMap<>();
			context.put("domain", domain);
			context.put("location", Map.of("country", Map.of("code", countryCode), "city", Map.of("code", cityCode)));
			context.put("transaction_id", transactionId);
			context.put("message_id", messageId);
			context.put("action", "search");
			context.put("timestamp", timestamp);
			context.put("version", version);
			context.put("bap_uri", bapUri);
			context.put("bap_id", bapId);
			context.put("ttl", "PT10M");

			Map<String, Object> buyerFinderFees = Map.of("descriptor", Map.of("code", "BUYER_FINDER_FEES"), "display",
					false, "list",
					List.of(Map.of("descriptor", Map.of("code", "BUYER_FINDER_FEES_TYPE"), "value",
							"percent-annualized"),
							Map.of("descriptor", Map.of("code", "BUYER_FINDER_FEES_PERCENTAGE"), "value", "1")));

			Map<String, Object> settlementTerms = Map.of("descriptor", Map.of("code", "SETTLEMENT_TERMS"), "display",
					false, "list",
					List.of(Map.of("descriptor", Map.of("code", "DELAY_INTEREST"), "value", "2.5"),
							Map.of("descriptor", Map.of("code", "STATIC_TERMS"), "value",
									"https://bap.credit.becknprotocol.io/personal-banking/loans/personal-loan"),
							Map.of("descriptor", Map.of("code", "OFFLINE_CONTRACT"), "value", "true")));

			Map<String, Object> message = Map.of("intent",
					Map.of("category", Map.of("descriptor", Map.of("code", "PERSONAL_LOAN")), "payment",
							Map.of("collected_by", "BPP", "tags", List.of(buyerFinderFees, settlementTerms))));

			requestBody.put("context", context);
			requestBody.put("message", message);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));

			// here i will call the function to create Authorization header

			long created = System.currentTimeMillis() / 1000L;
			long expires = created + 300000;
//            logger.info(toBase64(generateBlakeHash(req.get("value").toString())));
//            logger.info(req.get("value").toString());
			String hashedReq = Utils.hashMassage(objectMapper.writeValueAsString(requestBody), created, expires);
			String signature = Utils.sign(Base64.getDecoder().decode(BASE64_PRIVATE_KEY), hashedReq.getBytes());
			String subscriberId = SUBSCRIBER_ID;
			String uniqueKeyId = UNIQUE_KEY_ID;

			String authorizationSignature = "Signature keyId=\"" + subscriberId + "|" + uniqueKeyId + "|" + "ed25519\""
					+ ",algorithm=\"ed25519\"," + "created=\"" + created + "\",expires=\"" + expires
					+ "\",headers=\"(created) (expires)" + " digest\",signature=\"" + signature + "\"";

			// TODO: Add your signed Authorization header here
			headers.set("Authorization", authorizationSignature);

			HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

			// Load the userInfo to save the userInfo id into journeyLog
			UserInfo userInfo;
			Optional<UserInfo> optionalUserInfo = userInfoRepository.findByMobileNumber(mobileNumber);
			if (optionalUserInfo.isEmpty()) {
				throw new UserInfoNotFoundException("UserInfo not found with mobileNumber : " + mobileNumber);

			}
			userInfo = optionalUserInfo.get();

			// logic to save the journey log
			JourneyLog journeyLog = new JourneyLog();
			journeyLog.setPlatformId("O");
			journeyLog.setRequestId(gatewayUrl);
			journeyLog.setStage(stage);
			journeyLog.setUId(transactionId);
			journeyLog.setUser(userInfo);
			journeyLogRepository.save(journeyLog);

			// here we will save this api call in logger
			Logger logger = new Logger();
			logger.setJourneyLog(journeyLog);
//            logger.setUrl(gatewayUrl);// this url doesnt refers the value of api url it holds the url if we get from response of that api
			logger.setRequestPayload(String.valueOf(request));

			loggerRepository.save(logger);

			ResponseEntity<String> gatewayResponse = restTemplate.postForEntity(gatewayUrl, request, String.class);
			
			//here we will call our netweork observability function
			try {
				String response = txnLogService.pushTxnLogs("search", request);
				System.out.println("The Network Observability response we got in search is : "+response);
			}catch(Exception e) {
				e.printStackTrace();
			}
			

			Map<String, Object> responseMap = new LinkedHashMap<>();
			responseMap.put("transaction_id", transactionId);
			responseMap.put("message_id", messageId);
			responseMap.put("timestamp", timestamp);
			responseMap.put("gateway_response", objectMapper.readValue(gatewayResponse.getBody(), Object.class));

			// code to check the dob format and then setting the dd-mm-yyyy
			// format------------------------------

			String dobRaw = userInfo.getDob(); // The original DOB value
			LocalDate dobDate = null;

			// Try parsing the date with possible known formats
			DateTimeFormatter[] knownFormats = new DateTimeFormatter[] { DateTimeFormatter.ofPattern("yyyy-MM-dd"), // common
																													// API
																													// format
					DateTimeFormatter.ofPattern("dd/MM/yyyy"), // slash format
					DateTimeFormatter.ofPattern("MM-dd-yyyy"), // US format
					DateTimeFormatter.ofPattern("dd-MM-yyyy") // Desired format (already correct)
			};

			for (DateTimeFormatter format : knownFormats) {
				try {
					dobDate = LocalDate.parse(dobRaw, format);
					break; // successfully parsed
				} catch (DateTimeParseException e) {
					// try next
				}
			}

			if (dobDate == null) {
				throw new RuntimeException("Unrecognized date format: " + dobRaw);
			}

			// Now format it to dd-MM-yyyy
//            String dobFormatted = dobDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
			String dobFormatted = dobDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            String dobFormatted = dobDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//            ondcFormDataDTO.setDob(dobFormatted);

			// ----------------------------------------------------------------------------------------------------

			// FormDataDTO
			ONDCFormDataDTO ondcFormDataDTO = new ONDCFormDataDTO();

			ondcFormDataDTO.setPanName(userInfo.getFirstName() + " " + userInfo.getLastName());

			ondcFormDataDTO.setFirstName(userInfo.getFirstName());
			ondcFormDataDTO.setLastName(userInfo.getLastName());
//            ondcFormDataDTO.setDob(userInfo.getDob());
			ondcFormDataDTO.setDob(dobFormatted);

			ondcFormDataDTO
					.setGender(userInfo.getGender() == 1 ? "male" : userInfo.getGender() == 2 ? "female" : "other");
			ondcFormDataDTO.setPan(userInfo.getPan());
			ondcFormDataDTO.setContactNumber(userInfo.getMobileNumber());
			ondcFormDataDTO.setEmail(userInfo.getEmail());
//            ondcFormDataDTO.setOfficialemail(userInfo.getWorkEmail());
			ondcFormDataDTO.setOfficialEmail(userInfo.getWorkEmail());

			if (userInfo.getEmploymentType() == 1) {
//            	ondcFormDataDTO.setEmploymentType("Salaried");
				ondcFormDataDTO.setEmploymentType("salaried");
			} else if (userInfo.getEmploymentType() == 2) {
				ondcFormDataDTO.setEmploymentType("Self Employment");
			} else if (userInfo.getEmploymentType() == 3) {
				ondcFormDataDTO.setEmploymentType("Self Employment");// as ondc form has only two fields salaried and
																		// self employed otherwise here would be
																		// Business
			}

			ondcFormDataDTO.setEndUse("consumerDurablePurchase"); // if static
//            ondcFormDataDTO.setIncome(userInfo.getMonthlyIncome() != null ? userInfo.getMonthlyIncome().toString() : null);
			int inc = 0;
			if (userInfo.getMonthlyIncome() != null) {
			    try {
			        // Use BigDecimal to handle both integer and decimal values
			        BigDecimal income = new BigDecimal(userInfo.getMonthlyIncome().toString());
			        inc = income.intValue(); // safely converts (truncates decimal part if present)
			    } catch (NumberFormatException e) {
			        // log and keep inc = 0 if parsing fails
			        System.err.println("Invalid income format: " + userInfo.getMonthlyIncome());
			    }
			}
			
			ondcFormDataDTO.setIncome(String.valueOf(inc));
			ondcFormDataDTO.setCompanyName(userInfo.getCompanyName());
			ondcFormDataDTO.setUdyamNumber("UDYAM-ABC123"); // static or from another source
			ondcFormDataDTO.setAddressL1(userInfo.getAddress());
			ondcFormDataDTO.setAddressL2(""); // Optional field
//			ondcFormDataDTO.setCity("Pune"); // Static or derive if available
//			ondcFormDataDTO.setState("Maharashtra"); // Same
			ondcFormDataDTO.setCity("NA");
			ondcFormDataDTO.setState("NA");
			ondcFormDataDTO.setPincode(
					userInfo.getResidentialPincode() != null ? userInfo.getResidentialPincode().toString() : null);
			ondcFormDataDTO.setAa_id(userInfo.getMobileNumber() + "@finvu");
//            ondcFormDataDTO.setAa_id("")
			ondcFormDataDTO.setBureauConsent("on");

			// we will add this data in our responseMap to return it to frontend
			responseMap.put("ONDCFormData", ondcFormDataDTO);

			logger.setResponsePayload(String.valueOf(responseMap));
			loggerRepository.save(logger);

			return ResponseEntity.ok(responseMap);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Failed to send ONDC search", "details", e.getMessage()));
		}
	}

	// code to store search callback
	public ResponseEntity<?> onSearch(StringBuilder requestBody) throws JsonMappingException, JsonProcessingException {

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
		callback.setApi("/on_search");

//        callback.setProduct("ONDC");

//        System.out.println("The transaction id is : "+transactionId);
//        
//          try {
//        	  Thread.sleep(1500);
//          }catch(InterruptedException e)
//          {
//        	  Thread.currentThread().interrupt();//Restore Interupt flag
//        	  e.printStackTrace();
//          }

		// Broadcast to frontend subscribers
		messagingTemplate.convertAndSend("/topic/callbacks/" + transactionId, callback);

		callbackRepository.save(callback);
		
		//here we will call our netweork observability function
		try {
			String response = txnLogService.pushTxnLogs("on_search", requestBody.toString());
			System.out.println("The Network Observability response we got in on_search is "+response);
		}catch(Exception e) {
			e.printStackTrace();
		}

//    	return null;
//        return ResponseEntity.ok("ACK");
		return ResponseEntity.ok(Map.of("message", Map.of("ack", Map.of("status", "ACK"))));

	}

	public void writeFormLogs(String mobileNumber, String request, String response, String gatewayUrl, String transactionId, String formSubmissionStatus, String productName) {
		
		// Load the userInfo to save the userInfo id into journeyLog
					UserInfo userInfo;
					Optional<UserInfo> optionalUserInfo = userInfoRepository.findByMobileNumber(mobileNumber);
					if (optionalUserInfo.isEmpty()) {
						throw new UserInfoNotFoundException("UserInfo not found with mobileNumber : " + mobileNumber);

					}
					userInfo = optionalUserInfo.get();

					// logic to save the journey log
					JourneyLog journeyLog = new JourneyLog();
					journeyLog.setPlatformId("O");
					journeyLog.setRequestId(gatewayUrl);
					journeyLog.setStage(1);
					journeyLog.setUId(transactionId);
					journeyLog.setUser(userInfo);
					journeyLogRepository.save(journeyLog);

					// here we will save this api call in logger
					Logger logger = new Logger();
					logger.setJourneyLog(journeyLog);
//		            logger.setUrl(gatewayUrl);// this url doesnt refers the value of api url it holds the url if we get from response of that api
					logger.setRequestPayload(request);
					logger.setResponsePayload(response);

					loggerRepository.save(logger);
					
					if(formSubmissionStatus.equalsIgnoreCase("rejected")) {
						ApplyFail applyFail;
						
						
						Optional<ApplyFail> optionalApplyFail = applyFailRepository.findByUserAndProductName(userInfo, productName);
						if(optionalApplyFail.isEmpty()) {
							applyFail = new ApplyFail();
							applyFail.setJourneyLog(journeyLog);
							applyFail.setProductName(productName);
							applyFail.setUser(userInfo);
							applyFail.setUserMobileNumber(mobileNumber);
							
						}else {
							applyFail = optionalApplyFail.get();
							applyFail.setJourneyLog(journeyLog);
						}
						
						applyFailRepository.save(applyFail);
					}
		
	}

//	public void writeFormLogs(ONDCFormDataDTO ondcFormDataDTO) {
//		UserInfo userInfo;
//		Optional<UserInfo> optionalUserInfo = userInfoRepository.findByMobileNumber("8010489800");
//		if (optionalUserInfo.isEmpty()) {
//			throw new UserInfoNotFoundException("UserInfo not found with mobileNumber : " + "8010489800");
//
//		}
//		userInfo = optionalUserInfo.get();
//		
//		Logger logger = new Logger();
////		logger.setJourneyLog(journeyLog);
////        logger.setUrl(gatewayUrl);// this url doesnt refers the value of api url it holds the url if we get from response of that api
//		logger.setRequestPayload(String.valueOf(ondcFormDataDTO));
//		
//		System.out.println("Everything fine");
//	}

}
