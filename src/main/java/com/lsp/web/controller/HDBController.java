package com.lsp.web.controller;

import java.io.BufferedReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.io.FileReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
//
//import com.amazonaws.HttpMethod;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import com.lsp.web.ONDCService.HDBYubi;
import com.lsp.web.entity.Apply;
import com.lsp.web.entity.ApplyFail;
import com.lsp.web.entity.BankDetails;
import com.lsp.web.entity.JourneyLog;
import com.lsp.web.entity.Logger;
import com.lsp.web.entity.MasterBank;
import com.lsp.web.entity.Master_City_State;
import com.lsp.web.entity.ReferenceDetails;
import com.lsp.web.entity.SanctionDetails;
import com.lsp.web.entity.UserInfo;
import com.lsp.web.repository.UserInfoRepository;
import com.lsp.web.repository.ApplyFailRepository;
import com.lsp.web.repository.ApplyRepository;
import com.lsp.web.repository.BankDetailsRepository;
import com.lsp.web.repository.JourneyLogRepository;
import com.lsp.web.repository.LoggerRepository;
import com.lsp.web.repository.MasterBankRepository;
import com.lsp.web.repository.MasterCityStateRepository;
import com.lsp.web.repository.ReferenceDetailsRepository;
import com.lsp.web.repository.SanctionDetailsRepository;
import com.lsp.web.util.ObjResponse;

@RestController
public class HDBController {
	
	@Autowired
	UserInfoRepository userInfoRepository;
	
	@Autowired
	MasterCityStateRepository MasterCityStateRepository;
	
	@Autowired
	private JourneyLogRepository journeyLogRepository;
	
	@Autowired
	private LoggerRepository LoggerRepository;
	
	@Autowired
	private ApplyRepository applyRepository;
	
	@Autowired
	private BankDetailsRepository bankdetailsRepository;
	
	@Autowired
	private ApplyFailRepository applyFailRepository;
	
	@Autowired
	private SanctionDetailsRepository sanctionDetailsRepository;
	
	@Autowired
	private ReferenceDetailsRepository referenceDetailsRepository;
	
	@Autowired
	private MasterBankRepository masterBankRepository;

	
	@GetMapping("/createuid")
	public ResponseEntity<?> createId() {
		try {
			String transactionId = UUID.randomUUID().toString();
			return ResponseEntity.ok(transactionId);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create transactionId", "details", e.getMessage()));
		}
	}
	
	@PostMapping("/insertBanksFromExcel")
	public ResponseEntity<String> insertBanksFromExcel(@RequestParam("file") MultipartFile file) {
	    int insertedCount = 0;
	    int skippedCount = 0;

	    try (InputStream is = file.getInputStream();
	         Workbook workbook = new XSSFWorkbook(is)) {

	        Sheet sheet = workbook.getSheetAt(0);

	        for (Row row : sheet) {
	            if (row.getRowNum() == 0) { // Skip header
	                continue;
	            }

	            Cell cell = row.getCell(0);
	            if (cell != null) {
	                String bankName = cell.getStringCellValue().trim();
	                if (!bankName.isEmpty()) {
	                    // Skip if already exists
	                    if (!masterBankRepository.existsByBankName(bankName)) {
	                        com.lsp.web.entity.MasterBank bank = new com.lsp.web.entity.MasterBank();
	                        bank.setBankName(bankName);
	                        masterBankRepository.save(bank);
	                        insertedCount++;
	                    } else {
	                        skippedCount++;
	                    }
	                }
	            }
	        }

	        return ResponseEntity.ok(
	            "✅ Banks inserted: " + insertedCount + ", Skipped duplicates: " + skippedCount
	        );

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("❌ Failed to insert banks: " + e.getMessage());
	    }
	}

	
	@RestController
	@RequestMapping("/api/banks")
	@CrossOrigin(origins = "*")
	public class MasterBankController {

	    @Autowired
	    private MasterBankRepository bankRepository;

	    @GetMapping
	    public List<String> getAllBankNames() {
	        return bankRepository.findAll()
	                             .stream()
	                             .map(MasterBank::getBankName)
	                             .toList();
	    }
	}

	
	@GetMapping("/getMobileByClientLoanId")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public ResponseEntity<String> getMobileByClientLoanId(@RequestParam String clientLoanId) {
	    try {
	        List<JourneyLog> journeyLogs = journeyLogRepository.findByUIdOrderByCreateTimeDesc(clientLoanId);

	        if (journeyLogs == null || journeyLogs.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("JourneyLog not found");
	        }

	        JourneyLog latestLog = journeyLogs.get(0); // Get latest by createTime DESC

	        if (latestLog.getUser() == null || latestLog.getUser().getMobileNumber() == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mobile number not found");
	        }

	        String mobile = latestLog.getUser().getMobileNumber();
	        return ResponseEntity.ok(mobile);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error fetching mobile: " + e.getMessage());
	    }
	}

	 
	 @RequestMapping("/h5/sms_pl_journey")
	 @ResponseBody
	 @CrossOrigin(origins = "*")
	 public Object sms_pl_journey(
	         @RequestParam(required = false) String phone,
	         @RequestParam(required = false) String dsa) {

	     try {
	         Integer dsa_int = Integer.parseInt(dsa);
	         if (dsa_int == 214394238) {
	             send_next_SMS(phone, null); // pass null if no link needed
	         }
	     } catch (Exception ex) {
	         ex.printStackTrace();
	     }
	     return null;
	 }

	
	 public static JSONObject send_next_SMS(String phone, String url) {
		    try {
		        HttpClient httpClient = HttpClients.createDefault();
		        HttpPost httpPost = new HttpPost("https://bulksmsapi.vispl.in");

		        JSONObject jsonBody = new JSONObject();
		        jsonBody.put("username", "vibhuotp");
		        jsonBody.put("password", "vib@1234");
		        jsonBody.put("senderId", "CDSave");
		        jsonBody.put("messageType", "text");
		        jsonBody.put("ContentID", "1107170314630325855");
		        jsonBody.put("EntityID", "1201161304403738311");

		        JSONArray messages = new JSONArray();
		        JSONObject messageObj = new JSONObject();
		        messageObj.put("msisdn", phone);
		        messageObj.put("messageText", "Dear User, Thank you for registering on CreditHaat. Based on your profile you can avail loan Up to  Rs.5 lac. Complete application here - \"+url+\" -CreditHaat");
		        messages.put(messageObj);

		        jsonBody.put("message", messages);

		        StringEntity entity = new StringEntity(jsonBody.toString());
		        httpPost.setEntity(entity);
		        httpPost.setHeader("Content-Type", "application/json");

		        HttpResponse response = httpClient.execute(httpPost);
		        String responseBody = EntityUtils.toString(response.getEntity());

		        JSONObject responseJson = new JSONObject(responseBody);
		        responseJson.put("STATUS", "OK");
		        return responseJson;

		    } catch (Exception e) {
		        JSONObject errorJson = new JSONObject();
		        try {
		            errorJson.put("STATUS", "ERROR");
		            errorJson.put("message", "EXCEPTION: " + e.getMessage());
		        } catch (Exception ex) {
		        }
		        return errorJson;
		    }
		}


	@GetMapping("/sanction/{clientLoanId}")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public ResponseEntity<SanctionDetails> getSanctionByClientLoanId(@PathVariable String clientLoanId) {
//	    Optional<SanctionDetails> sanction = sanctionDetailsRepository.findByClientLoanId(clientLoanId);
		Optional<SanctionDetails> sanction = sanctionDetailsRepository.findTopByClientLoanIdOrderByCreateTimeDesc(clientLoanId);
	    if (sanction.isPresent()) {
	        return ResponseEntity.ok(sanction.get());
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}
	
	@GetMapping("/sanctionWithAccount/{clientLoanId}")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public ResponseEntity<Map<String, Object>> getSanctionWithAccount(@PathVariable String clientLoanId) {
	    Map<String, Object> result = new HashMap<>();

	    Optional<SanctionDetails> sanctionOpt =
	        sanctionDetailsRepository.findTopByClientLoanIdOrderByCreateTimeDesc(clientLoanId);

	    if (sanctionOpt.isPresent()) {
	        SanctionDetails sanction = sanctionOpt.get();

	        // Add all sanction fields
	        result.put("loanAmount", sanction.getLoanAmount());
	        result.put("tenure", sanction.getTenure());
	        result.put("interestRate", sanction.getInterestRate());
	        // add more fields if needed...

	        // ✅ Add account number
	        List<JourneyLog> logs = journeyLogRepository.findByUIdOrderByCreateTimeDesc(clientLoanId);
	        if (logs != null && !logs.isEmpty()) {
	            JourneyLog latestLog = logs.get(0); // pick the latest
	            if (latestLog.getUser() != null) {
	                bankdetailsRepository.findByUser(latestLog.getUser())
	                    .ifPresent(bd -> result.put("accountNumber", bd.getAccountNumber()));
	            }
	        }

	        return ResponseEntity.ok(result);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}

	@RestController
	@RequestMapping("/api/master")
	@CrossOrigin(origins = "*")
	public class MasterCityStateController {

	    private final MasterCityStateRepository repository;

	    public MasterCityStateController(MasterCityStateRepository repository) {
	        this.repository = repository;
	    }

	    @GetMapping("/cities")
	    public List<String> getCities() {
	        return repository.findAllDistinctCities();
	    }
	}


	
	@RequestMapping("/createHDBLoan")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> createHDBLoan(@RequestParam String mobileNumber) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
//            UserInfo user = (UserInfo) dao.get(UserInfo.class, "mobilenumber='" + mobileNumber + "'");
//            if (user == null) {
//                resp.setCode(-1);
//                resp.setMsg("User not found");
//                return resp;
//            }
        	Optional<UserInfo> optionalUserInfo =  userInfoRepository.findByMobileNumber(mobileNumber);

        	if(optionalUserInfo.isEmpty()) {
        		ApplyFail applyFail = new ApplyFail();
                applyFail.setUserMobileNumber(mobileNumber);
                applyFail.setProductName("HDB");
                applyFail.setCreateTime(LocalDateTime.now());
                applyFailRepository.save(applyFail);

                resp.setCode(-1);
                resp.setMsg("User not found");
                return resp;
        	}
        	
        	UserInfo user = optionalUserInfo.get();
        	
        	Optional<Master_City_State> cityStateOpt = MasterCityStateRepository.findByPincode(user.getResidentialPincode());

        	if (cityStateOpt.isEmpty()) {
        		Optional<Apply> existingApply = applyRepository.findByUser(user);
            	Apply apply = existingApply.orElse(new Apply());
//            	apply.setStatus("Reject");
                apply.setStage(99);
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
                
        		ApplyFail applyFail = new ApplyFail();
                applyFail.setUserMobileNumber(user.getMobileNumber());
                applyFail.setProductName("HDB");
                applyFail.setCreateTime(LocalDateTime.now());
                applyFailRepository.save(applyFail);
        	    resp.setCode(-1);
        	    resp.setMsg("Pincode not found");
        	    return resp;
        	}

        	// Now safely get the entity:
        	Master_City_State cityStateHdb = cityStateOpt.get();
        	
        	JSONObject requestPayload = new JSONObject();
        	requestPayload.put("first_name", user.getFirstName());
        	requestPayload.put("last_name", user.getLastName());
        	requestPayload.put("middle_name", user.getFatherName());
        	requestPayload.put("phone", user.getMobileNumber());
        	requestPayload.put("dob", user.getDob());
        	requestPayload.put("gender", user.getGender());
        	requestPayload.put("address", user.getAddress());
        	requestPayload.put("city", cityStateHdb.getCity());
        	requestPayload.put("state", cityStateHdb.getState());
        	requestPayload.put("pincode", user.getResidentialPincode());
        	requestPayload.put("company_name", user.getCompanyName());
        	requestPayload.put("monthly_income", user.getMonthlyIncome());
        	requestPayload.put("marital_status", user.getMaritalStatus());
        	requestPayload.put("pan", user.getPan());
        	requestPayload.put("loan_amount", user.getLoanAmount());
        	requestPayload.put("email", user.getEmail());

        	JourneyLog journeyLog = new JourneyLog();
            journeyLog.setUser(user); // FK to UserInfo
            journeyLog.setStage(1);
            journeyLog.setUId("");  // Save the clientLoanId as UId (transaction id)
            journeyLog.setPlatformId("Y");  // Any other platform info if needed

            journeyLogRepository.save(journeyLog);
            
        	// ✅ Save Logger with request only (response comes later)
        	Logger logger = new Logger();
        	logger.setJourneyLog(journeyLog);
        	logger.setRequestPayload(requestPayload.toString());
        	logger.setCreateTime(LocalDateTime.now());
        	logger = LoggerRepository.save(logger);
        	

        	Map<String, Object> result = HDBYubi.create_application_with_details(
        			user.getFirstName(),                              // String
        		    user.getLastName(),                               // String
        		    user.getFatherName(),                             // String (as middlename)
        		    user.getMobileNumber(),                           // String
        		    user.getDob(),                                     // String
        		    user.getGender(),               // Integer (from "m"/"f" → 1/2/3)
        		    user.getAddress(),                                // String
        		    cityStateHdb.getCity(),                           // String
        		    cityStateHdb.getState(),                          // String
        		    user.getResidentialPincode(),   // Integer
        		    user.getCompanyName(),                            // String
        		    user.getMonthlyIncome(),        // Float
        		    user.getMaritalStatus(),        // Integer (1-4)
        		    user.getMobileNumber(),                           // String (again for phone field)
        		    user.getPan(),                                    // String
        		    user.getLoanAmount(),           // Float
        		    user.getEmail()          
            );

        	if (result == null || result.get("client_loan_id") == null || result.get("client_loan_id").toString().isEmpty()) {
        		Optional<Apply> existingApply = applyRepository.findByUser(user);
            	Apply apply = existingApply.orElse(new Apply());
//            	apply.setStatus("Reject");
                apply.setStage(99);
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
                
        		ApplyFail applyFail = new ApplyFail();
                applyFail.setUserMobileNumber(user.getMobileNumber());
                applyFail.setProductName("HDB");
                applyFail.setJourneyLog(journeyLog);
                applyFail.setCreateTime(LocalDateTime.now());
                applyFailRepository.save(applyFail);
        		resp.setCode(-1);
        	    resp.setMsg("Loan creation failed");
        	    resp.setObj(result);
        	    return resp;
        	}
        	
        	  logger.setUrl(result.get("url").toString());
              logger.setRequestPayload(result.get("requestPayload").toString());
              logger.setResponsePayload(result.get("responsePayload").toString());
              logger.setJourneyLog(journeyLog); 

              LoggerRepository.save(logger);
              
        	String clientLoanId = result.get("client_loan_id").toString();

        	if (user != null) {
        		String productName = "HDB"; // or use dynamic value
        	    Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
                apply.setUrl("");
                apply.setJourneyLog(journeyLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }
        	
//            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setUser(user); // FK to UserInfo
            journeyLog.setStage(1);
            journeyLog.setUId(clientLoanId); 
            journeyLog.setPlatformId("Y");  

            journeyLogRepository.save(journeyLog);
            
//            Logger logger = new Logger();


            resp.setCode(0);
            resp.setMsg("Loan created");
            Map<String, Object> combined = new HashMap<>();
            combined.put("client_loan_id", clientLoanId);
            resp.setObj(combined);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Internal error occurred.");
        }
        return resp;
    }

    
    @RequestMapping("/getHDBLoanStatus")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> getHDBLoanStatus(@RequestParam String clientLoanId) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
        	  Optional<JourneyLog> optionalExistingLog = journeyLogRepository.findByUId(clientLoanId);
        	 
        	  JourneyLog existingLog = optionalExistingLog.get();
              UserInfo user = existingLog.getUser();
              
        	  if (optionalExistingLog.isEmpty()) {
            	  Optional<Apply> existingApply = applyRepository.findByUser(user);
              	Apply apply = existingApply.orElse(new Apply());
//              	apply.setStatus("Reject");
                  apply.setStage(99);
                  apply.setUpdateTime(LocalDateTime.now());
                  if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                  applyRepository.save(apply);
                  
                  ApplyFail applyFail = new ApplyFail();
                  applyFail.setUserMobileNumber(user.getMobileNumber());
                  applyFail.setProductName("HDB");
                  applyFail.setCreateTime(LocalDateTime.now());
                  applyFail.setJourneyLog(existingLog);
                  applyFail.setUser(user); // ✅ set user
                  applyFail.setApply(apply);
                  applyFailRepository.save(applyFail);
                  resp.setCode(-1);
                  resp.setMsg("Journey not found.");
                  return resp;
              }

            
            JourneyLog journeyLog = new JourneyLog();
            
            journeyLog.setStage(2);
            journeyLog.setUId(clientLoanId);  
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y"); 
            journeyLogRepository.save(journeyLog);
            
            Logger logger = new Logger();
            logger.setRequestPayload("https://colend-uat-01-api.go-yubi.in/colending/clients/vibhuprada/api/v2/loans/" + clientLoanId + "/get_status");
            logger.setCreateTime(LocalDateTime.now()); // since it's GET, no payload
            logger.setJourneyLog(existingLog); // set after retrieving journeyLog

            LoggerRepository.save(logger);
            JSONObject statusResult = HDBYubi.getApplicationStatus(clientLoanId);
            
//            Optional<JourneyLog> optionalExistingLog = journeyLogRepository.findByUId(clientLoanId);
//            if (optionalExistingLog.isEmpty()) {
//                resp.setCode(-1);
//                resp.setMsg("JourneyLog not found for clientLoanId=" + clientLoanId);
//                return resp;
//            }

//            JourneyLog existingLog = optionalExistingLog.get();
//            UserInfo user = existingLog.getUser();
            
            String responseString = statusResult != null ? statusResult.toString() : "";

            if (!responseString.contains("loan_details")) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
            	Apply apply = existingApply.orElse(new Apply());
//            	apply.setStatus("Reject");
                apply.setStage(99);
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
                
            	ApplyFail applyFail = new ApplyFail();
                applyFail.setUserMobileNumber(user.getMobileNumber());
                applyFail.setProductName("HDB");
                applyFail.setCreateTime(LocalDateTime.now());
                applyFail.setJourneyLog(existingLog);
                applyFail.setUser(user); // ✅ set user
                applyFail.setApply(apply);
                applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Loan status could not be verified.");
                return resp;
            }

            // Example: parse status from loan_details
//            String status = statusResult.optJSONObject("loan_details").optString("status");

//            Optional<JourneyLog> optionalExistingLog = journeyLogRepository.findByUId(clientLoanId);
            if (optionalExistingLog.isEmpty()) {
                resp.setCode(-1);
                resp.setMsg("JourneyLog not found for clientLoanId=" + clientLoanId);
                return resp;
            }
            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
                apply.setUrl("");
                apply.setJourneyLog(existingLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }


//            JourneyLog existingLog = optionalExistingLog.get();
//            UserInfo user = existingLog.getApplyRecord();
//            JourneyLog journeyLog = new JourneyLog();
            
//            journeyLog.setStage(2);
//            journeyLog.setUId(clientLoanId);  
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y"); 
//            journeyLogRepository.save(journeyLog);
            
//            Logger logger = new Logger();
            logger.setRequestPayload("https://colend-uat-01-api.go-yubi.in/colending/clients/vibhuprada/api/v2/loans/" + clientLoanId + "/get_status");
            logger.setRequestPayload(null); // since it's GET, no payload
            logger.setResponsePayload(statusResult != null ? statusResult.toString() : "null");
            logger.setJourneyLog(existingLog); // set after retrieving journeyLog

            LoggerRepository.save(logger);

            resp.setCode(0);
            resp.setMsg("Status retrieved");
//            Map<String, Object> data = new HashMap<>();
//            data.put("status", status);
//            data.put("loan_details", statusResult);
//            resp.setObj(data);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Internal error occurred.");
        }
        return resp;
    }

    
    @RequestMapping("/initiateHDBAA")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> initiateHDBAA(@RequestParam String clientLoanId,
                                             @RequestParam String callbackStatus) {
        ObjResponse<Object> resp = new ObjResponse<>();
        Map<String, Object> payload = new HashMap<>();
        try {
            // ✅ 1. Validate callbackStatus
            if (callbackStatus == null || callbackStatus.trim().isEmpty()) {
                resp.setCode(-1);
                resp.setMsg("Skipping AA initiation. callbackStatus is empty.");
                return resp;
            }
            
            List<JourneyLog> stage2Logs = journeyLogRepository.findByUIdAndStage(clientLoanId, 1);

            if (stage2Logs == null || stage2Logs.isEmpty()) {
                resp.setCode(-1);
                resp.setMsg("No Stage 2 log found for clientLoanId=" + clientLoanId);
                return resp;
            }

            JourneyLog lastStage2Log = stage2Logs.get(0); // Most recent Stage 2
            UserInfo user = lastStage2Log.getUser();
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            requestPayload.put("callbackStatus", callbackStatus);
            
            List<JourneyLog> existingLogs = journeyLogRepository.findFirstByUId(clientLoanId);
            if (existingLogs == null || existingLogs.isEmpty()) {
                resp.setCode(-1);
                resp.setMsg("JourneyLog not found for clientLoanId=" + clientLoanId);
                return resp;
            }

            JourneyLog existingLog = existingLogs.get(0);
            
            JourneyLog journeyLog = new JourneyLog(); 

            // ✅ 4. Update JourneyLog with new AA stage
            journeyLog.setStage(2);
            journeyLog.setUId(clientLoanId); 
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y"); 
            journeyLogRepository.save(journeyLog);
            
            Logger logger = new Logger();
            logger.setRequestPayload("requestPayload".toString());
            logger.setJourneyLog(existingLog);
            LoggerRepository.save(logger);  

            // ✅ 2. Call HDBYubi to initiate AA
            Map<String, Object> aaMap = HDBYubi.initiateAA(clientLoanId);
            
            JSONObject responsePayload = (JSONObject) aaMap.get("responsePayload");

         // Check if expected keys like "request_Id" or "loan_details" are present
         if (responsePayload == null || !responsePayload.has("request_Id")) {
        	 Optional<Apply> existingApply = applyRepository.findByUser(user);
         	Apply apply = existingApply.orElse(new Apply());
//         	apply.setStatus("Reject");
             apply.setStage(99);
             apply.setUpdateTime(LocalDateTime.now());
             if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
             applyRepository.save(apply);
             
            	ApplyFail applyFail = new ApplyFail();
                applyFail.setUserMobileNumber(user.getMobileNumber());
                applyFail.setProductName("HDB");
                applyFail.setJourneyLog(journeyLog);
                applyFail.setCreateTime(LocalDateTime.now());
                applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Failed to initiate AA.");
                return resp;
            }

            JSONObject aaResult = (JSONObject) aaMap.get("responsePayload");


            int requestId = aaResult.optInt("request_Id");
            String redirectionUrl = aaResult.optString("redirection_url");

            // ✅ 3. Find JourneyLog by UId (clientLoanId)
         // ✅ 3. Find the most recent Stage 2 log

//            List<JourneyLog> existingLogs = journeyLogRepository.findFirstByUId(clientLoanId);
//            if (existingLogs == null || existingLogs.isEmpty()) {
//                resp.setCode(-1);
//                resp.setMsg("JourneyLog not found for clientLoanId=" + clientLoanId);
//                return resp;
//            }
//
//            JourneyLog existingLog = existingLogs.get(0); // or apply logic to get the latest one

            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
//                apply.setUrl("http://localhost:3000/yubi/YubiSteps?client_loan_id=" + clientLoanId);
                apply.setUrl(redirectionUrl);
                apply.setJourneyLog(existingLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }
            
//            JourneyLog journeyLog = new JourneyLog(); 

            // ✅ 4. Update JourneyLog with new AA stage
            journeyLog.setStage(2);
            journeyLog.setUId(clientLoanId);  
            journeyLog.setRequestId(String.valueOf(requestId));
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y"); 
            journeyLogRepository.save(journeyLog);
            
//            Logger logger = new Logger();
            logger.setRequestPayload(aaMap.get("requestPayload").toString());
            logger.setResponsePayload(aaResult.toString());
            logger.setUrl(aaMap.get("url").toString());
            logger.setJourneyLog(existingLog);
            LoggerRepository.save(logger);  


            Map<String, Object> data = new HashMap<>();
            data.put("request_id", requestId);
            data.put("redirection_url", redirectionUrl);

            resp.setCode(0);
            resp.setMsg("AA initiated");
            resp.setObj(data);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Internal error occurred.");
        }
        return resp;
    }


    
// -----------------------to fetch requestid for the retrieve api ---------------------------   
    
    @RequestMapping(value = "/getRequestIdByClientLoanId", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> getRequestIdByClientLoanId(@RequestParam String clientLoanId) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            // ✅ Use your JourneyLogRepository to find by UId
        	  List<JourneyLog> logs = journeyLogRepository.findByUIdOrderByCreateTimeDesc(clientLoanId);

//        	  Optional<JourneyLog> optionalExistingLog = journeyLogRepository.findByUId(clientLoanId);
        	  List<JourneyLog> optionalExistingLog = journeyLogRepository.findByUIdAndStage(clientLoanId, 2);
        	 
        	  JourneyLog existingLog = optionalExistingLog.get(0);
              UserInfo user = existingLog.getUser();
              
              if (logs.isEmpty()) {
            	  Optional<Apply> existingApply = applyRepository.findByUser(user);
              	Apply apply = existingApply.orElse(new Apply());
//              	apply.setStatus("Reject");
                  apply.setStage(99);
                  apply.setUpdateTime(LocalDateTime.now());
                  if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                  applyRepository.save(apply);
                  
                  ApplyFail applyFail = new ApplyFail();
                  applyFail.setUserMobileNumber(user.getMobileNumber());
                  applyFail.setProductName("HDB");
                  applyFail.setJourneyLog(existingLog);
                  applyFail.setCreateTime(LocalDateTime.now());
                  applyFailRepository.save(applyFail);
                  resp.setCode(-1);
                  resp.setMsg("JourneyLog not found for client loan ID.");
                  return resp;
              }

//              JourneyLog latestLog = logs.get(0);  // Most recent log
//              String requestId = latestLog.getRequestId();
              String requestId = existingLog.getRequestId();
              
            if (requestId == null || requestId.trim().isEmpty()) {
            	 Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(existingLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                   resp.setCode(-1);
                   resp.setMsg("Request ID not found for client loan ID.");
                   return resp;
            } else {
                resp.setCode(0);
                resp.setMsg("Found request ID");
                resp.setObj(requestId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Internal server error");
        }
        return resp;
    }

    
    @PostMapping("/retrieveReport")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> retrieveReport(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");
            Object requestIdObj = payload.get("requestId");
            Integer requestId = null;
            if (requestIdObj instanceof Integer) {
                requestId = (Integer) requestIdObj;
            } else if (requestIdObj instanceof String) {
                requestId = Integer.parseInt((String) requestIdObj);
            } else {
                throw new IllegalArgumentException("Invalid requestId type");
            }

            // ✅ Get most recent Stage 3 JourneyLog
            List<JourneyLog> stage3Logs = journeyLogRepository.findByUIdAndStage(clientLoanId, 2);
            
            JourneyLog stage3Log = stage3Logs.get(0);
            UserInfo user = stage3Log.getUser();
            
            if (stage3Logs == null || stage3Logs.isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(stage3Log);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("No Stage 3 log found for clientLoanId=" + clientLoanId);
                return resp;
            }
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            requestPayload.put("request_id", requestId);
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(3);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(stage3Log);
            LoggerRepository.save(logger); 
            

            // ✅ Call HDBYubi to retrieve report
            JSONObject json = HDBYubi.retrieveReport(clientLoanId, requestId);
            
            logger.setRequestPayload(json.get("requestPayload").toString());
            logger.setResponsePayload(json.get("responsePayload").toString());
            logger.setUrl("");
            LoggerRepository.save(logger);

            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
                apply.setUrl("");
                apply.setJourneyLog(stage3Log);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }

            // ✅ Save new log for Stage 4
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(4);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);
            


            resp.setCode(0);
            resp.setMsg("Retrieved report successfully");
            // resp.setObj(json.toMap());  // If needed
        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error retrieving report");
        }
        return resp;
    }

    
    
//    @PostMapping("/initiateKYC")
//    @ResponseBody
//    @CrossOrigin(origins = "*")
//    public ObjResponse<Object> initiateKYC(@RequestBody Map<String, Object> payload) {
//        ObjResponse<Object> resp = new ObjResponse<>();
//        try {
//            String clientLoanId = (String) payload.get("clientLoanId");
//            JSONObject result = HDBYubi.initiateKYC(clientLoanId);
//
//            // ✅ Extract the redirect_url field from the Yubi response
//            String redirectUrl = result.optString("redirect_url");
//
//            if (redirectUrl != null && !redirectUrl.isEmpty()) {
//                resp.setCode(0);
//                resp.setMsg("KYC initiated successfully");
//                resp.setObj(redirectUrl);  // ✅ Now your frontend gets the redirect URL
//            } else {
//                resp.setCode(-1);
//                resp.setMsg("No redirect URL found in KYC response");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.setCode(-1);
//            resp.setMsg("Error initiating KYC");
//        }
//        return resp;
//    }
    
    @PostMapping("/initiateKYC")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> initiateKYC(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");
            String callbackStatus = (String) payload.get("callbackStatus");

            // ✅ 1. Validate callbackStatus
            if (callbackStatus == null || callbackStatus.trim().isEmpty()) {
                resp.setCode(-1);
                resp.setMsg("Skipping KYC initiation: callbackStatus is empty.");
                return resp;
            }

            // ✅ 2. Check if latest stage is "Stage 4" before proceeding
            List<JourneyLog> existingLogs = journeyLogRepository.findByUIdAndStage(clientLoanId, 3);
            
            JourneyLog previousLog = existingLogs.get(0); // latest "Stage 4"
            UserInfo user = previousLog.getUser();
            
            if (existingLogs.isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(previousLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Cannot initiate KYC: Stage 4 not completed for clientLoanId=" + clientLoanId);
                return resp;
            }
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(4);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger); 

            // ✅ 3. Call initiateKYC API
            JSONObject result = HDBYubi.initiateKYC(clientLoanId);
            String redirectUrl = result.optString("redirect_url", result.optString("redirection_url"));

            if (redirectUrl == null || redirectUrl.trim().isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(journeyLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("No redirect URL found in KYC response.");
                return resp;
            }
            
            logger.setRequestPayload(result.getString("requestPayload").toString());
            logger.setResponsePayload(result.get("responsePayload").toString());
            logger.setUrl(redirectUrl);
            LoggerRepository.save(logger);
            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
//                apply.setUrl("http://localhost:3000/yubi/Selfiepage?client_loan_id=" + clientLoanId);
                apply.setUrl(redirectUrl);
                apply.setJourneyLog(previousLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }

            // ✅ 4. Create Stage 5 entry
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(5);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);


            // ✅ 5. Return success
            resp.setCode(0);
            resp.setMsg("KYC initiated successfully");
            resp.setObj(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error initiating KYC");
        }
        return resp;
    }

    

    @PostMapping("/updateKYC")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ObjResponse<Object> updateKYC(@RequestBody Map<String, String> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = payload.get("clientLoanId");
            String selfieImageUrl = payload.get("selfieImageUrl");

            // ✅ 1. Check if previous stage (Stage 5) exists
            List<JourneyLog> existingLogs = journeyLogRepository.findByUIdAndStage(clientLoanId, 4);
            
            JourneyLog previousLog = existingLogs.get(0); // latest Stage 5
            UserInfo user = previousLog.getUser();
            
            if (existingLogs.isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(previousLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Cannot update KYC: Stage 5 not completed for clientLoanId=" + clientLoanId);
                return resp;
            }


            if (user == null) {
                resp.setCode(-1);
                resp.setMsg("User info not found for this JourneyLog");
                return resp;
            }
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(5);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);

            // ✅ 2. Update selfieUrl in UserInfo
         // ✅ 2. Update selfieUrl in BankDetails instead of UserInfo
            Optional<BankDetails> bankDetailsOpt = bankdetailsRepository.findByUser(user);
            BankDetails bankDetails = bankDetailsOpt.orElse(new BankDetails());
            bankDetails.setUser(user);
            bankDetails.setSelfieUrl(selfieImageUrl);
            bankdetailsRepository.save(bankDetails);


            // ✅ 3. Call Yubi KYC update API
            JSONObject result = HDBYubi.updateKYC(clientLoanId, selfieImageUrl);
            
            logger.setRequestPayload(result.getString("requestPayload").toString());
            logger.setResponsePayload(result.get("responsePayload").toString());
            logger.setUrl("");
            LoggerRepository.save(logger);
            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
                apply.setUrl("http://localhost:3000/yubi/Loanapprovalpage?client_loan_id=" + clientLoanId);            
                apply.setJourneyLog(previousLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }


            // ✅ 4. Create new entry for Stage 6
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(6);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);

            // ✅ 5. Return success
            resp.setCode(0);
            resp.setMsg("KYC updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error updating KYC");
        }
        return resp;
    }


    
//    @RestController
    @CrossOrigin(origins = "*")
    @ResponseBody
//    public class SelfieController {

        @GetMapping("/generatePresignedUrl")
        public Map<String, Object> generatePresignedUrl(@RequestParam String fileName) {
            Map<String, Object> response = new HashMap<>();
            try {
                String bucketName = "shitaltestinsight";
                String folderName = "selfies";  // Optional: subfolder to keep things organized
                String objectKey = folderName + "/" + fileName;

                // ⏳ Presigned URL valid for 5 minutes
                Date expiration = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

                // ✅ Your credentials
                AWSCredentials credentials = new BasicAWSCredentials(
                    "xyz",  // Replace with your Access Key
                    "abc"   // Replace with your Secret Key
                );

                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withRegion("ap-south-1")
                        .build();

                // ✅ Generate the presigned PUT URL
                GeneratePresignedUrlRequest generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(bucketName, objectKey)
                                .withMethod(HttpMethod.PUT)
                                .withExpiration(expiration);

                URL presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

                // ✅ Construct the public URL for your record
                String publicUrl = String.format("https://%s.s3.ap-south-1.amazonaws.com/%s", bucketName, objectKey);

                Map<String, String> obj = new HashMap<>();
                obj.put("presignedUrl", presignedUrl.toString());
                obj.put("publicUrl", publicUrl);

                response.put("code", 0);
                response.put("msg", "Success");
                response.put("obj", obj);

            } catch (Exception e) {
                e.printStackTrace();
                response.put("code", -1);
                response.put("msg", "Error generating presigned URL");
            }
            return response;
        }
//    }

    
    @PostMapping("/submitBankDetails")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> submitBankDetails(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");
            String bankName = (String) payload.get("bankName");
            String accountName = (String) payload.get("accountName");
            String ifscCode = (String) payload.get("ifscCode");
            String accountNo = (String) payload.get("accountNumber");
            String branchName = (String) payload.get("branchName");

            // ✅ 1. Get latest Stage 6 JourneyLog for this clientLoanId
            List<JourneyLog> logs = journeyLogRepository.findByUIdAndStage(clientLoanId, 5);
            
            JourneyLog previousLog = logs.get(0); // Assuming latest is first
            UserInfo user = previousLog.getUser();
            
            if (logs.isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(previousLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Stage 6 not completed for clientLoanId=" + clientLoanId);
                return resp;
            }


            if (user == null) {
                resp.setCode(-1);
                resp.setMsg("User info not found for this JourneyLog");
                return resp;
            }

            // ✅ 2. Save bank details to UserInfo
         // ✅ 2. Save bank details to BankDetails table
            Optional<BankDetails> existingBankDetails = bankdetailsRepository.findByUser(user);
            BankDetails bankDetails = existingBankDetails.orElse(new BankDetails());

            bankDetails.setUser(user);
            bankDetails.setBankName(bankName);
            bankDetails.setAccountHolderName(accountName);
            bankDetails.setIfsc(ifscCode);
            bankDetails.setAccountNumber(accountNo);
            bankDetails.setBranchName(branchName);

            bankdetailsRepository.save(bankDetails);


            // ✅ 3. Call disbursement API via RestTemplate
//            double loanAmountDouble = Double.parseDouble(user.getLoanAmount().trim());
            double loanAmountDouble = user.getLoanAmount() != null ? user.getLoanAmount().doubleValue() : 0.0;

            int loanAmount = (int) loanAmountDouble;
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("bank_name", bankName);
            requestPayload.put("account_name", accountName);
            requestPayload.put("ifsc_code", ifscCode);
            requestPayload.put("bank_branch_name", branchName);
            requestPayload.put("account_no", accountNo);
            requestPayload.put("Amount", loanAmount);
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(6);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);

            JSONObject result = HDBYubi.callDisbursementAPI(
                clientLoanId,
                loanAmount,
                bankName,
                accountName,
                ifscCode,
                branchName,
                accountNo
            );
            
            logger.setRequestPayload(result.getString("requestPayload").toString());
            logger.setResponsePayload(result.get("responsePayload").toString());
            logger.setUrl("");
            LoggerRepository.save(logger);
            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
                apply.setUrl("http://localhost:3000/yubi/Referencedetailspage?client_loan_id=" + clientLoanId);
                apply.setJourneyLog(previousLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }

            // ✅ 4. Create new JourneyLog for Stage 7
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(7);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);


            resp.setCode(0);
            resp.setMsg("Bank details submitted and disbursement API called successfully");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Internal server error");
        }
        return resp;
    }

    
    @PostMapping("/submitReferenceDetails")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> submitReferenceDetails(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");
            String mothersName = (String) payload.get("mothersName");
            String yearOfExperience = (String) payload.get("yearOfExperience");

            String ref1Name = (String) payload.get("ref1Name");
            String ref1Mobile = (String) payload.get("ref1Mobile");
            String ref1Address = (String) payload.get("ref1Address");

            String ref2Name = (String) payload.get("ref2Name");
            String ref2Mobile = (String) payload.get("ref2Mobile");
            String ref2Address = (String) payload.get("ref2Address");

            String salarySlipLink = (String) payload.get("salarySlipLink");

            Object loanAmountObj = payload.get("loanAmount");
            Object tenureObj = payload.get("tenure");
            
            Object interestRateObj = payload.get("interestRate");
            double interestRate = 20.0; // fallback
            if (interestRateObj != null) {
                interestRate = Double.parseDouble(interestRateObj.toString());
            }
           
            int finalLoanAmount = 0;
            int tenure = 0;

            if (loanAmountObj instanceof Number) {
                finalLoanAmount = ((Number) loanAmountObj).intValue();
            } else if (loanAmountObj instanceof String) {
                finalLoanAmount = Integer.parseInt((String) loanAmountObj);
            }

            if (tenureObj instanceof Number) {
                tenure = ((Number) tenureObj).intValue();
            } else if (tenureObj instanceof String) {
                tenure = Integer.parseInt((String) tenureObj);
            }
            String ref1Relation = extractAndMapRelation(payload.get("ref1Relation"));
            String ref2Relation = extractAndMapRelation(payload.get("ref2Relation"));

            // ✅ Get JourneyLog for Stage 6
            List<JourneyLog> logs = journeyLogRepository.findByUIdAndStage(clientLoanId, 6);
            if (logs.isEmpty()) {
                resp.setCode(-1);
                resp.setMsg("Stage 6 not completed for clientLoanId=" + clientLoanId);
                return resp;
            }

            JourneyLog previousLog = logs.get(0);
            UserInfo user = previousLog.getUser();

            if (user == null) {
                resp.setCode(-1);
                resp.setMsg("User info not found");
                return resp;
            }
            Optional<BankDetails> bankDetailsOpt = bankdetailsRepository.findByUser(user);
            BankDetails bankDetails = bankDetailsOpt.orElse(new BankDetails());

            String selfieUrl = bankDetails.getSelfieUrl();
//            String employmentAddress = user.getAddress();
//            int pincode = user.getResidentialPincode() != null ? user.getResidentialPincode() : 0;
            int pincode = user.getWorkPincode() != null ? user.getWorkPincode() : 0;


            Optional<Master_City_State> cityStateOpt = MasterCityStateRepository.findByPincode(user.getWorkPincode());

        	if (cityStateOpt.isEmpty()) {
        		Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(previousLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
        	    resp.setCode(-1);
        	    resp.setMsg("Pincode not found");
        	    return resp;
        	}

        	// Now safely get the entity:
        	Master_City_State cityStateHdb = cityStateOpt.get();

            String city = cityStateHdb.getCity();
            String state = cityStateHdb.getState();
            
            String employmentAddress = city + ", " + state + " - " + pincode;
            
            String spouseName = (user.getMaritalStatus() != null && user.getMaritalStatus() == 1) ? user.getSpouseName() : null;

            // ✅ Update user info
            user.setMotherName(mothersName);
            userInfoRepository.save(user);
            
            user.setYOE(yearOfExperience);
            bankdetailsRepository.save(bankDetails);

            // ✅ Prepare API Call
            List<String> photoLinks = List.of(selfieUrl);
            List<String> payslipLinks = List.of(salarySlipLink);
            List<Map<String, String>> references = List.of(
                Map.of("name", ref1Name, "phone", ref1Mobile, "relationship", ref1Relation, "address", ref1Address),
                Map.of("name", ref2Name, "phone", ref2Mobile, "relationship", ref2Relation, "address", ref2Address)
            );
            
         // ✅ Save reference 1
            ReferenceDetails ref1Entity = new ReferenceDetails();
            ref1Entity.setClientLoanId(clientLoanId);
            ref1Entity.setName(ref1Name);
            ref1Entity.setPhone(ref1Mobile);
            ref1Entity.setRelationship(ref1Relation);
            ref1Entity.setAddress(ref1Address);
            referenceDetailsRepository.save(ref1Entity);

            // ✅ Save reference 2
            ReferenceDetails ref2Entity = new ReferenceDetails();
            ref2Entity.setClientLoanId(clientLoanId);
            ref2Entity.setName(ref2Name);
            ref2Entity.setPhone(ref2Mobile);
            ref2Entity.setRelationship(ref2Relation);
            ref2Entity.setAddress(ref2Address);
            referenceDetailsRepository.save(ref2Entity);

            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            requestPayload.put("principal_amount", finalLoanAmount);
            requestPayload.put("photo_link", photoLinks);
            requestPayload.put("payslip_link", payslipLinks);
            requestPayload.put("references", references);
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(7);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);

            JSONObject result = HDBYubi.callUpdateLoanAPI(
                clientLoanId, finalLoanAmount, photoLinks, payslipLinks,
                employmentAddress, city, state, pincode,
                mothersName, Integer.parseInt(yearOfExperience),
                spouseName, references, tenure, interestRate
            );

            logger.setRequestPayload(result.getString("requestPayload").toString());
            logger.setResponsePayload(result.get("responsePayload").toString());
            logger.setUrl("");
            LoggerRepository.save(logger);
            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
                apply.setUrl("http://localhost:3000/yubi/Finalsanctiondetails?client_loan_id=" + clientLoanId);
                apply.setJourneyLog(previousLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }

            // ✅ Add JourneyLog for Stage 8
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(8);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);

            resp.setCode(0);
            resp.setMsg("Reference details submitted and API called successfully");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error saving reference details");
        }
        return resp;
    }

    private int parseInt(Object object) {
		// TODO Auto-generated method stub
		return 0;
	}


	/**
     * Safely extract and map relation value to your API code.
     */
    private String extractAndMapRelation(Object relationObj) {
        if (relationObj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) relationObj;
            Object value = map.get("value");
            if (value != null) {
                return mapRelationValueToCode(value.toString());
            }
        } else if (relationObj instanceof String) {
            return mapRelationValueToCode((String) relationObj);
        }
        return null; // fallback
    }

    /**
     * Map app relation value to your API relation code.
     */
    private String mapRelationValueToCode(String value) {
        if (value == null) return null;
        switch (value.trim().toLowerCase()) {
            case "father": return "FTH";  // example
            case "mother": return "MTH";
            case "friend": return "F";
            case "relative": return "R";
            default: return "O"; // Other or fallback
        }
    }

    
    @PostMapping("/generateKfsDocument")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> generateKfsDocument(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");
            
            List<JourneyLog> logs = journeyLogRepository.findByUIdAndStage(clientLoanId, 7);
            
            JourneyLog previousLog = logs.get(0); // Assuming latest is first
            UserInfo user = previousLog.getUser();
            
            if (logs.isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(previousLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Stage 6 not completed for clientLoanId=" + clientLoanId);
                return resp;
            }
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(8);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);
            
            JSONObject result = HDBYubi.generateKfsDocument(clientLoanId);
            
            logger.setResponsePayload(result.get("response").toString());
            logger.setUrl("");
            LoggerRepository.save(logger);
            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
                apply.setUrl("http://localhost:3000/yubi/Waitingpageloanagreement?client_loan_id=" + clientLoanId);
                apply.setJourneyLog(previousLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }

            
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(9);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);
            
            
            resp.setCode(0);
            resp.setMsg("Generate KFS Document API called successfully");
//            resp.setObj(result.toMap());
        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error calling Generate KFS Document API");
        }
        return resp;
    }
    
    @PostMapping("/generateLoanAgreementDocument")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> generateLoanAgreementDocument(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");
            
            List<JourneyLog> logs = journeyLogRepository.findByUIdAndStage(clientLoanId, 8);
            
            JourneyLog previousLog = logs.get(0); // Assuming latest is first
            UserInfo user = previousLog.getUser();
            
            if (logs.isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(previousLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Stage 9 not completed for clientLoanId=" + clientLoanId);
                return resp;
            }
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(9);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);
            
            JSONObject result = HDBYubi.generateLoanAgreementDocument(clientLoanId);
            
            logger.setResponsePayload(result.get("response").toString());
            logger.setUrl("");
            LoggerRepository.save(logger);
            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
                apply.setUrl("");
                apply.setJourneyLog(previousLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }
            
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(10);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);

            
            resp.setCode(0);
            resp.setMsg("Loan Agreement Document API called successfully");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error calling Loan Agreement Document API");
        }
        return resp;
    }

    
    @PostMapping("/requestEsign")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> requestEsign(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");

            // ✅ Fetch latest JourneyLog entry of previous stage (Stage 7)
            String previousStage = "Stage 7";
            List<JourneyLog> previousLogs = journeyLogRepository.findByUIdAndStage(clientLoanId, 9);
            if (previousLogs == null || previousLogs.isEmpty()) {
                resp.setCode(-1);
                resp.setMsg("Previous stage JourneyLog not found");
                return resp;
            }

            // ✅ Get latest JourneyLog and UserInfo
            JourneyLog previousLog = previousLogs.get(0);
            UserInfo user = previousLog.getUser();
            if (user == null) {
                resp.setCode(-1);
                resp.setMsg("User info not found");
                return resp;
            }

            Optional<BankDetails> bankDetailsOpt = bankdetailsRepository.findByUser(user);
            BankDetails bankDetails = bankDetailsOpt.orElse(new BankDetails());
            
            // ✅ Get details from UserInfo
            String email = user.getEmail();
            String phone = user.getMobileNumber();
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String selfieurl = bankDetails.getSelfieUrl(); // Not used here but available
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(10);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            requestPayload.put("email", email);
            requestPayload.put("phone_number", phone);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);

            // ✅ Call Yubi eSign API
            JSONObject result = HDBYubi.requestEsign(clientLoanId, email, phone, firstName, lastName);

            if (result == null) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(journeyLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);

                resp.setCode(-1);
                resp.setMsg("eSign API did not return URL, saved in apply_fail");
                return resp;
            }
            // ✅ Extract redirect URL
            String redirectUrl = result
            	    .optJSONObject("eStampLeegalityAPIResponse")
            	    .optJSONObject("messageBody")
            	    .optJSONObject("responseDetails")
            	    .optJSONArray("invitation_detail")
            	    .optJSONObject(0)
            	    .optString("signUrl", "");
            
            logger.setRequestPayload(result.get("requestPayload").toString());
            logger.setResponsePayload(result.get("responsePayload").toString());
            logger.setUrl(redirectUrl);
            LoggerRepository.save(logger);
            
            if (user != null) {
            	String productName = "HDB"; // or use dynamic value
                Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
                Apply apply = existingApply.orElse(new Apply());
                apply.setUser(user);
                apply.setProductName("HDB"); // Replace with actual product name
                apply.setMobileNumber(user.getMobileNumber());
//                apply.setUrl("http://localhost:3000/yubi/Agreementcompleted?client_loan_id=" + clientLoanId);
                apply.setUrl(redirectUrl);
                apply.setJourneyLog(previousLog);  // ✅ set FK
                apply.setStage(journeyLog.getStage());
                apply.setUpdateTime(LocalDateTime.now());
                if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                applyRepository.save(apply);
            }

            // ✅ Save new JourneyLog for Stage 8
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(11);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);


            // ✅ Return success
            resp.setCode(0);
            resp.setMsg("Request eSign API called successfully");
            resp.setObj(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error calling eSign API");
        }
        return resp;
    }
    
    
    @PostMapping("/registerMandate")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> registerMandate(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");

            // ✅ Fetch latest JourneyLog entry of previous stage (Stage 10 or eSign)
            List<JourneyLog> previousLogs = journeyLogRepository.findByUIdAndStage(clientLoanId, 10);
           
            JourneyLog previousLog = previousLogs.get(0);
            UserInfo user = previousLog.getUser();
            
            if (previousLogs == null || previousLogs.isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(previousLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Previous stage JourneyLog not found");
                return resp;
            }

            if (user == null) {
                resp.setCode(-1);
                resp.setMsg("User info not found");
                return resp;
            }

            Optional<BankDetails> bankDetailsOpt = bankdetailsRepository.findByUser(user);
            BankDetails bankDetails = bankDetailsOpt.orElse(new BankDetails());
            
            String Accountno = bankDetails.getAccountNumber();
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(11);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            requestPayload.put("account_no", Accountno);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);
            
         // Fetch SanctionDetails using clientLoanId
//            Optional<SanctionDetails> sanctionOpt = sanctionDetailsRepository.findByClientLoanId(clientLoanId);
            Optional<SanctionDetails> sanctionOpt = sanctionDetailsRepository.findTopByClientLoanIdOrderByCreateTimeDesc(clientLoanId);
            if (!sanctionOpt.isPresent()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Sanction details not found");
                return resp;
            }

            SanctionDetails sanction = sanctionOpt.get();
            Float loanAmount = sanction.getLoanAmount();

            
            // ✅ Call Yubi Register Mandate API
            JSONObject result = HDBYubi.Register_Mandate(clientLoanId, Accountno, loanAmount);

            if (result == null || !result.has("mandate_url") || result.optString("mandate_url").isEmpty()) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(journeyLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
            	resp.setCode(-1);
                resp.setMsg("Mandate API call failed or URL missing");
                return resp;
            }
            
            String redirectUrl = result.getString("mandate_url");
            
            logger.setRequestPayload(result.get("requestPayload").toString());
            logger.setResponsePayload(result.get("responsePayload").toString());
            logger.setUrl(redirectUrl);
            LoggerRepository.save(logger);

            // ✅ Update Apply Table
            String productName = "HDB"; // or use dynamic value
            Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
            Apply apply = existingApply.orElse(new Apply());
            apply.setUser(user);
            apply.setProductName("HDB");
            apply.setMobileNumber(user.getMobileNumber());
            apply.setUrl(redirectUrl);
            apply.setJourneyLog(previousLog);  // ✅ set FK
            apply.setStage(journeyLog.getStage());
            apply.setUpdateTime(LocalDateTime.now());
            if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
            applyRepository.save(apply);

            // ✅ Save new JourneyLog for Stage 12 (Mandate)
//            JourneyLog journeyLog = new JourneyLog();
//            journeyLog.setStage(12);
//            journeyLog.setUId(clientLoanId);
//            journeyLog.setUser(user);
//            journeyLog.setPlatformId("Y");
//            journeyLogRepository.save(journeyLog);

            // ✅ Respond
            resp.setCode(0);
            resp.setMsg("Mandate API called successfully");
            resp.setObj(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error calling Mandate API");
        }
        return resp;
    }

    @PostMapping("/getMandateStatus")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> getMandateStatus(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");
            
            List<JourneyLog> previousLogs = journeyLogRepository.findByUIdAndStage(clientLoanId, 11);
            
            JourneyLog previousLog = previousLogs.get(0);
            UserInfo user = previousLog.getUser();
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(12);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);

            JSONObject result = HDBYubi.GetMandateStatus(clientLoanId);
            
            logger.setRequestPayload(result.get("requestPayload").toString());
            logger.setResponsePayload(result.get("responsePayload").toString());
            logger.setUrl("");
            LoggerRepository.save(logger);

            if (result == null || result.optJSONObject("responsePayload") == null) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(journeyLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
            	resp.setCode(-1);
                resp.setMsg("Mandate status check failed");
                return resp;
            }

            String productName = "HDB"; // or use dynamic value
            Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
            Apply apply = existingApply.orElse(new Apply());
            apply.setUser(user);
            apply.setProductName("HDB");
            apply.setMobileNumber(user.getMobileNumber());
            apply.setUrl("");
            apply.setJourneyLog(previousLog);  // ✅ set FK
            apply.setStage(journeyLog.getStage());
            apply.setUpdateTime(LocalDateTime.now());
            if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
            applyRepository.save(apply);
            
            // ✅ Log or save result if needed
            resp.setCode(0);
            resp.setMsg("Mandate status checked successfully");
//            resp.setObj(result);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error during mandate status check");
        }

        return resp;
    }

    
    @PostMapping("/agreementSigned")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ObjResponse<Object> agreementSigned(@RequestBody Map<String, Object> payload) {
        ObjResponse<Object> resp = new ObjResponse<>();
        try {
            String clientLoanId = (String) payload.get("clientLoanId");
            
   List<JourneyLog> previousLogs = journeyLogRepository.findByUIdAndStage(clientLoanId, 12);
            
            JourneyLog previousLog = previousLogs.get(0);
            UserInfo user = previousLog.getUser();
            
            JourneyLog journeyLog = new JourneyLog();
            journeyLog.setStage(13);
            journeyLog.setUId(clientLoanId);
            journeyLog.setUser(user);
            journeyLog.setPlatformId("Y");
            journeyLogRepository.save(journeyLog);
            
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("clientLoanId", clientLoanId);
            
            Logger logger = new Logger();
            logger.setRequestPayload(requestPayload.toString());
            logger.setJourneyLog(previousLog);
            LoggerRepository.save(logger);

            JSONObject result = HDBYubi.sendAgreementSignedAPI(clientLoanId);
            
            logger.setRequestPayload(result.get("requestPayload").toString());
            logger.setResponsePayload(result.get("responsePayload").toString());
            logger.setUrl("");
            LoggerRepository.save(logger);

            if (result == null || result.optJSONObject("responsePayload") == null) {
            	Optional<Apply> existingApply = applyRepository.findByUser(user);
               	Apply apply = existingApply.orElse(new Apply());
//               	apply.setStatus("Reject");
                   apply.setStage(99);
                   apply.setUpdateTime(LocalDateTime.now());
                   if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
                   applyRepository.save(apply);
                   
                   ApplyFail applyFail = new ApplyFail();
                   applyFail.setUserMobileNumber(user.getMobileNumber());
                   applyFail.setProductName("HDB");
                   applyFail.setJourneyLog(journeyLog);
                   applyFail.setCreateTime(LocalDateTime.now());
                   applyFailRepository.save(applyFail);
                resp.setCode(-1);
                resp.setMsg("Agreement signing failed");
                return resp;
            }

            String productName = "HDB"; // or use dynamic value
            Optional<Apply> existingApply = applyRepository.findByUserAndProductName(user, productName);
            Apply apply = existingApply.orElse(new Apply());
            apply.setUser(user);
            apply.setProductName("HDB");
            apply.setMobileNumber(user.getMobileNumber());
            apply.setUrl("");
            apply.setJourneyLog(previousLog);  // ✅ set FK
            apply.setStage(200);
            apply.setUpdateTime(LocalDateTime.now());
            if (apply.getCreateTime() == null) apply.setCreateTime(LocalDateTime.now());
            applyRepository.save(apply);
            
            // ✅ Optionally update logs, Apply, JourneyLog here
            resp.setCode(0);
            resp.setMsg("Agreement signed successfully");
            resp.setObj(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            resp.setCode(-1);
            resp.setMsg("Error during agreement signing");
        }

        return resp;
    }


}
