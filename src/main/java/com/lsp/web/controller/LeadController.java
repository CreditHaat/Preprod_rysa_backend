//package com.lsp.web.controller;
//
//import java.io.BufferedReader;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//import net.sf.json.JSONObject;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//public class LeadController {
//	
//	@RequestMapping(value = "api/leadcreate", method = RequestMethod.POST)
//	@ResponseBody
//	public void registerEmbeddedNew(@RequestHeader(required = true) String token, HttpServletResponse response, HttpServletRequest request) {
//
//	    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//	    JSONObject jsonObject = null;
//	    StringBuffer jb = new StringBuffer();
//	    String line = null;
//	    Map<String, Object> result = new HashMap<>();
//
//	    String firstname = null, lastname = null, mobilenumber = null, dob = null,
//	           occupation = null, paymentType = null, pan = null, gender = null,
//	           residentialPincode = null, email = null;
//
//	    Float monthlyIncome = null, loanAmount = null;
//
//	    // Optional fields
//	    String spouseName = null, maritalStatus = null, address = null,
//	           companyName = null, workEmail = null, workPincode = null;
//
//	    boolean emailValid = false;
//	    Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
//
//	    if ("Y3JlZGl0aGFhdHRlc3RzZXJ2ZXI=".equals(token) || "Y3JlZGl0aGFhdHByb2RzZXJ2ZXI=".equals(token)) {
//	        try {
//	            BufferedReader reader = request.getReader();
//	            while ((line = reader.readLine()) != null) jb.append(line);
//
//	            if (!jb.toString().isEmpty()) {
//	                jsonObject = JSONObject.fromObject(jb.toString());
//
//	                // Required fields
//	                mobilenumber = jsonObject.optString("mobilenumber");
//	                if (StringUtil.nullOrEmpty(mobilenumber)) throw new BadRequestException("mobilenumber is required");
//
//	                firstname = jsonObject.optString("firstname");
//	                if (StringUtil.nullOrEmpty(firstname)) throw new BadRequestException("firstname is required");
//
//	                lastname = jsonObject.optString("lastname");
//	                if (StringUtil.nullOrEmpty(lastname)) throw new BadRequestException("lastname is required");
//
//	                dob = jsonObject.optString("dob");
//	                if (StringUtil.nullOrEmpty(dob)) throw new BadRequestException("dob is required");
//
//	                occupation = jsonObject.optString("occupation");
//	                if (StringUtil.nullOrEmpty(occupation)) throw new BadRequestException("occupation is required");
//
//	                paymentType = jsonObject.optString("paymentType");
//	                if (StringUtil.nullOrEmpty(paymentType)) throw new BadRequestException("paymentType is required");
//
//	                pan = jsonObject.optString("pan");
//	                if (StringUtil.nullOrEmpty(pan)) throw new BadRequestException("pan is required");
//
//	                gender = jsonObject.optString("gender");
//	                if (StringUtil.nullOrEmpty(gender)) throw new BadRequestException("gender is required");
//
//	                residentialPincode = jsonObject.optString("residentialPincode");
//	                if (StringUtil.nullOrEmpty(residentialPincode)) throw new BadRequestException("residentialPincode is required");
//
//	                email = jsonObject.optString("email");
//	                if (StringUtil.nullOrEmpty(email) || !emailPattern.matcher(email).matches()) {
//	                    throw new BadRequestException("email is invalid or missing");
//	                }
//
//	                try {
//	                    monthlyIncome = Float.valueOf(jsonObject.getString("monthlyIncome"));
//	                } catch (Exception ex) {
//	                    throw new BadRequestException("monthlyIncome must be a valid float");
//	                }
//
//	                try {
//	                    loanAmount = Float.valueOf(jsonObject.getString("loanAmount"));
//	                } catch (Exception ex) {
//	                    throw new BadRequestException("loanAmount must be a valid float");
//	                }
//
//	                // Optional fields
//	                spouseName = jsonObject.optString("spouseName", null);
//	                maritalStatus = jsonObject.optString("maritalStatus", null);
//	                address = jsonObject.optString("address", null);
//	                companyName = jsonObject.optString("companyName", null);
//	                workEmail = jsonObject.optString("workEmail", null);
//	                workPincode = jsonObject.optString("workPincode", null);
//
//	                // Save or update user
//	                UserInfo user = (UserInfo) dao.get(UserInfo.class, "mobilenumber='" + mobilenumber + "'");
//	                boolean isNew = false;
//
//	                if (user == null) {
//	                    user = new UserInfo();
//	                    user.setUserId("web" + UUID.randomUUID().toString().replaceAll("-", ""));
//	                    user.setRegTime(new Date());
//	                    user.setLast_attribution_time(new Date());
//	                    user.setActive(0);
//	                    isNew = true;
//	                }
//
//	                user.setFirstname(firstname);
//	                user.setLastname(lastname);
//	                user.setMobilenumber(mobilenumber);
//	                user.setDob(dob);
//	                user.setOccupation(occupation);
//	                user.setPaymentType(paymentType);
//	                user.setMonthlyIncome(monthlyIncome);
//	                user.setPan(pan);
//	                user.setGender(gender);
//	                user.setResidentialPincode(residentialPincode);
//	                user.setEmail(email);
//	                user.setLoanAmount(loanAmount);
//
//	                // Optional fields
//	                user.setSpouseName(spouseName);
//	                user.setMaritalStatus(maritalStatus);
//	                user.setAddress(address);
//	                user.setCompanyName(companyName);
//	                user.setWorkEmail(workEmail);
//	                user.setWorkPincode(workPincode);
//
//	                dao.saveOrUpdate(user);
//
//	                Map<String, Object> data = new HashMap<>();
//	                data.put("registertime", new Date().toString());
//	                data.put("message", isNew ? "User registered" : "User updated");
//
//	                result.put("code", 200);
//	                result.put("data", data);
//
//	                response.addIntHeader("code", 200);
//	                response.addHeader("message", "Ok");
//	                response.getWriter().print(new org.json.simple.JSONObject(result));
//	            }
//
//	        } catch (BadRequestException e) {
//	            throw new BadRequestException(e.getMessage());
//	        } catch (Exception e) {
//	            throw new RuntimeException(e.getMessage());
//	        }
//	    } else {
//	        throw new BadRequestException("Unauthorized token");
//	    }
//	}
//
//
//}

package com.lsp.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsp.web.entity.JourneyLog;
import com.lsp.web.entity.UserInfo;
import com.lsp.web.repository.JourneyLogRepository;
import com.lsp.web.repository.UserInfoRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "*")
public class LeadController {
	
	@Autowired
	private UserInfoRepository userInfoRepository;
	
	@Autowired
	private JourneyLogRepository journeyLogRepository;


	@CrossOrigin(origins = "*")
    @RequestMapping(value = "api/leadcreate", method = RequestMethod.POST)
    public void registerEmbeddedNew(

            @RequestHeader(required = true) String token,
            HttpServletResponse response,
            HttpServletRequest request) {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        StringBuilder jb = new StringBuilder();
        String line;
        Map<String, Object> result = new HashMap<>();

        String firstname = null, lastname = null, mobilenumber = null, dob = null,
                occupation = null, pan = null, gender = null,
                email = null, father_name = null;
		Integer residentialPincode = null;
		Integer paymentType = null;

        Float monthlyIncome = null, loanAmount = null;

        // Optional fields
        String spouseName = null, maritalStatus = null, address = null,
                company_name = null, workEmail = null;
		Integer workPincode = null;

        Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");

        if ("Y3JlZGl0aGFhdHRlc3RzZXJ2ZXI=".equals(token) ||
            "Y3JlZGl0aGFhdHByb2RzZXJ2ZXI=".equals(token)) {

            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) jb.append(line);

                if (!jb.toString().isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonObject = mapper.readTree(jb.toString());

                    mobilenumber = getText(jsonObject, "mobilenumber", true);
                    firstname = getText(jsonObject, "firstname", true);
                    lastname = getText(jsonObject, "lastname", true);
                    dob = getText(jsonObject, "dob", true);
                    
                    String occupationStr = getText(jsonObject, "occupation", true);
                    Integer employmentTypeCode = mapEmploymentType(occupationStr);
                    if (employmentTypeCode == null) {
                        throw new BadRequestException("occupation must be one of: salaried, self_employed, others");
                    }
                    
                    String paymentTypeStr = getText(jsonObject, "paymentType", true);
                    try {
                        paymentType = Integer.valueOf(paymentTypeStr);
                    } catch (NumberFormatException e) {
                        throw new BadRequestException("paymentType must be an integer");
                    }
                    
                    String residentialPincodeStr = getText(jsonObject, "residentialPincode", true);
                    try {
                        residentialPincode = Integer.valueOf(residentialPincodeStr);
                    } catch (NumberFormatException e) {
                        throw new BadRequestException("residentialPincode must be an integer");
                    }
                    
                    pan = getText(jsonObject, "pan", true);
                    String genderStr = getText(jsonObject, "gender", true);
                    Integer genderCode = mapGender(genderStr);
                    if (genderCode == null) {
                        throw new BadRequestException("gender must be 'm', 'f', or 'o'");
                    }
//                    gender = genderCode.toString();
                    
                    email = getText(jsonObject, "email", true);
                    father_name = getText(jsonObject, "father_name", true);

                    if (!emailPattern.matcher(email).matches()) {
                        throw new BadRequestException("email is invalid or missing");
                    }

                    try {
                        monthlyIncome = Float.valueOf(jsonObject.path("monthlyIncome").asText());
                    } catch (Exception e) {
                        throw new BadRequestException("monthlyIncome must be a valid float");
                    }

                    try {
                        loanAmount = Float.valueOf(jsonObject.path("loanAmount").asText());
                    } catch (Exception e) {
                        throw new BadRequestException("loanAmount must be a valid float");
                    }

                    // Optional fields
                    spouseName = getText(jsonObject, "spouseName", false);
                    String maritalStatusStr = getText(jsonObject, "maritalStatus", false);
                    Integer maritalStatusCode = null;
                    if (maritalStatusStr != null) {
                        try {
                            maritalStatusCode = mapMaritalStatus(maritalStatusStr);
                        } catch (IllegalArgumentException e) {
                            throw new BadRequestException("Invalid maritalStatus. Use married, unmarried, divorced, widowed");
                        }
                    }
                    
                    
                    address = getText(jsonObject, "address", false);
                    company_name = getText(jsonObject, "company_name", false);
                    workEmail = getText(jsonObject, "workEmail", false);
                    String workPincodeStr = getText(jsonObject, "workPincode", false);
                    if (workPincodeStr != null) {
                        try {
                            workPincode = Integer.valueOf(workPincodeStr);
                        } catch (NumberFormatException e) {
                            throw new BadRequestException("workPincode must be an integer");
                        }
                    }


                    // Dummy DAO logic (replace with real DAO usage)
                    
                    Optional<UserInfo> optionalUserInfo = userInfoRepository.findByMobileNumber(mobilenumber);
                    UserInfo user = null;
                    boolean isNew = false;
                    
                    if(optionalUserInfo.isEmpty()) {
                    	user = new UserInfo();
//                    	user.setUserId("web" + UUID.randomUUID().toString().replaceAll("-", ""));
//                        user.setRegTime(new Date());
//                        user.setLast_attribution_time(new Date());
                        user.setActive(0);
                        isNew = true;
                    }else if(optionalUserInfo.isPresent()) {
                    	user = optionalUserInfo.get();
                    	user.setUpdateTime(LocalDateTime.now());
                    }

                    // Set values
                    user.setFirstName(firstname);
                    user.setFatherName(father_name);
                    user.setLastName(lastname);
                    user.setMobileNumber(String.valueOf(mobilenumber));
                    user.setDob(dob);
                    user.setEmploymentType(employmentTypeCode);  // now saving integer
                    user.setPaymentType(paymentType);
                    user.setMonthlyIncome(monthlyIncome);
                    user.setLoanAmount(loanAmount);
                    user.setPan(pan);
                    user.setGender(genderCode);
                    user.setResidentialPincode(residentialPincode);
                    user.setEmail(email);

                    user.setSpouseName(spouseName);
                    try {
                    	user.setMaritalStatus(maritalStatusCode);
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestException("Invalid maritalStatus (must be: married, unmarried, divorced, widowed)");
                    }
                    user.setAddress(address);
                    user.setCompanyName(company_name);
                    user.setWorkEmail(workEmail);
                    user.setWorkPincode(workPincode);
                    
                    userInfoRepository.save(user);
                    
                    JourneyLog journeyLog = new JourneyLog();
                    journeyLog.setUser(user); // FK to UserInfo
                    journeyLog.setStage(0);
                    journeyLog.setUId("");  // Save the clientLoanId as UId (transaction id)
                    journeyLog.setPlatformId("Y");  // Any other platform info if needed

                    journeyLogRepository.save(journeyLog);

                    // dao.saveOrUpdate(user);

                    Map<String, Object> data = new HashMap<>();
                    data.put("registertime", new Date().toString());
                    data.put("message", isNew ? "User registered" : "User updated");

                    result.put("code", 200);
                    result.put("data", data);

                    response.addIntHeader("code", 200);
                    response.addHeader("message", "Ok");

                    mapper.writeValue(response.getWriter(), result);
                }

            } catch (BadRequestException e) {
                throw new RuntimeException("Bad request: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Error: " + e.getMessage());
            }

        } else {
            throw new RuntimeException("Unauthorized token");
        }
    }

    private String getText(JsonNode json, String field, boolean required) throws BadRequestException {
        JsonNode node = json.get(field);
        if (node == null || node.isNull() || node.asText().trim().isEmpty()) {
            if (required) throw new BadRequestException(field + " is required");
            return null;
        }
        return node.asText().trim();
    }

    
    private Integer mapMaritalStatus(String status) {
        if (status == null) return null;

        switch (status.trim().toLowerCase()) {
            case "married":
                return 1;
            case "unmarried":
                return 2;
            case "divorced":
                return 3;
            case "widowed":
                return 4;
            default:
                throw new IllegalArgumentException("Unknown marital status: " + status);
        }
    }
    
    private Integer mapGender(String genderStr) {
        if (genderStr == null) return null;

        switch (genderStr.toLowerCase()) {
            case "m":
            case "male":
            case "Male":
                return 1;
            case "f":
            case "female":
            case "Female":
                return 2;
            case "o":
            case "other":
                return 3;
            default:
                return null;
        }
    }

    private Integer mapEmploymentType(String occupationStr) {
        if (occupationStr == null) return null;

        switch (occupationStr.trim().toLowerCase()) {
            case "salaried":
            case "Salaried":	
                return 1;
            case "self_employed":
            case "self employed":
            case "Self employed":	
                return 2;
            case "Business":
            case "other":
                return 3;
            default:
                return null;
        }
    }


}
