package com.lsp.web.controller;

import java.util.Date;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lsp.web.Exception.ProductNotFoundExeption;
import com.lsp.web.entity.Callback;
import com.lsp.web.entity.Product;
import com.lsp.web.entity.SanctionDetails;
import com.lsp.web.repository.CallbackRepository;
import com.lsp.web.repository.ProductRepository;
import com.lsp.web.repository.SanctionDetailsRepository;

@RestController
public class HDBCallbackController {
	
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private SanctionDetailsRepository sanctionDetailsRepository;

	
//HDB_ Webhook_API
    
    @RequestMapping(value="/callback/web/Yubi_Sanction")
    @ResponseBody
    public String Hdb_status(@RequestBody String payload){
    	
    	try {
			JSONObject json = new JSONObject(payload);
			String clientLoanId = json.optString("client_loan_id");
			Callback callback = new Callback();
			
//			   callback = new Callback();
//			   callback.setClickTime(new Date));
//			   callback.setClickid(json.optString("client_loan_id"));
//			   callback.setProduct((Product)dao.get(Product.class,"productName='HDB'"));
//			   callback.setCallbackLocation("/callback/web/Yubi_Sanction");
//			   callback.setCallback(1);
//			   callback.setCallbackContent(payload);
//			   dao.saveOrUpdate(callback);
			
			Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
			if(optionalProduct.isEmpty()) {
				throw new ProductNotFoundExeption("Product not found with name HDB");
			}
			callback.setProduct(optionalProduct.get());
			callback.setApi("/callback/web/Yubi_Sanction");
			callback.setContent(payload);
			callback.setuID(json.optString("client_loan_id"));
			
			callbackRepository.save(callback);
			
//			JSONArray offers = json.optJSONArray("offers");
//	        if (offers != null && offers.length() > 0) {
//	            JSONObject offer = offers.getJSONObject(0);
//	            JSONArray slabs = offer.optJSONArray("slabs");
//	            if (slabs != null && slabs.length() > 0) {
//	                JSONObject slab = slabs.getJSONObject(0);
//
//	                String loanAmountStr = slab.optString("min_amount", "0");
//	                String tenureStr = slab.optString("tenure", "0");
//	                String interestStr = slab.optString("interest", "0");
//
//	                SanctionDetails sanction = new SanctionDetails();
//	                sanction.setClientLoanId(json.optString("client_loan_id"));
//	                sanction.setLoanAmount(Float.parseFloat(loanAmountStr));
//	                sanction.setTenure(Integer.parseInt(tenureStr));
//	                sanction.setInterestRate(Float.parseFloat(interestStr));
//	                sanction.setCallback(callback); // link to callback
//
//	                sanctionDetailsRepository.save(sanction);
			 // Parse sanction details
	        JSONArray offers = json.optJSONArray("offers");
	        if (offers != null && offers.length() > 0) {
	            JSONObject offer = offers.getJSONObject(0);
	            JSONArray slabs = offer.optJSONArray("slabs");
	            if (slabs != null && slabs.length() > 0) {
	                JSONObject slab = slabs.getJSONObject(0);

	                String loanAmountStr = slab.optString("min_amount", "0");
	                String tenureStr = slab.optString("tenure", "0");
	                String interestStr = slab.optString("interest", "0");

	                // ✅ Check if record already exists
	                SanctionDetails sanction;
	                Optional<SanctionDetails> optionalSanction = sanctionDetailsRepository.findTopByClientLoanIdOrderByCreateTimeDesc(clientLoanId);

	                if (optionalSanction.isPresent()) {
	                    sanction = optionalSanction.get(); // Update existing
	                } else {
	                    sanction = new SanctionDetails(); // Create new
	                    sanction.setClientLoanId(clientLoanId);
	                }

	                sanction.setLoanAmount(Float.parseFloat(loanAmountStr));
	                sanction.setTenure(Integer.parseInt(tenureStr));
	                sanction.setInterestRate(Float.parseFloat(interestStr));
	                sanction.setCallback(callback); // Link updated callback

	                sanctionDetailsRepository.save(sanction); // insert or update
	            }
	        }
			
			// Broadcast to frontend subscribers
	        messagingTemplate.convertAndSend("/topic/callbacks", callback);

			
		
		} catch (Exception ex) {}
    	return "ok";
    }
    
    @RequestMapping(value="/callback/web/Yubi_loan_status", method = RequestMethod.POST)
    @ResponseBody
    public String Hdb_Sanction(@RequestBody String payload) {

        try {
            JSONObject json = new JSONObject(payload);

            Callback callback = new Callback();
//            callback.setClickTime(new Date());
//            callback.setClickid(json.optString("client_loan_id"));
//            callback.setProduct((Product) dao.get(Product.class, "productName='HDB'"));
//            callback.setCallbackLocation("/callback/web/Yubi_loan_status");
//            callback.setCallback(1);
//            callback.setCallbackContent(payload);
//            dao.saveOrUpdate(callback);
            
            Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
			if(optionalProduct.isEmpty()) {
				throw new ProductNotFoundExeption("Product not found with name HDB");
			}
			callback.setProduct(optionalProduct.get());
			callback.setApi("/callback/web/Yubi_loan_status");
			callback.setContent(payload);
			callback.setuID(json.optString("client_loan_id"));
			
			callbackRepository.save(callback);
			
			// Broadcast to frontend subscribers
	        messagingTemplate.convertAndSend("/topic/callbacks", callback);

            // Return the "status" field from the payload
            return json.optString("status", "Unknown");
            
        } catch (Exception ex) {
            return "Error";
        }
    }

    
//    @RequestMapping(value = "/callback/web/Yubi_KYC_Status", method = RequestMethod.POST)
//    @ResponseBody
//    public String Yubi_KYC_Webhook(@RequestBody String payload) {
//        try {
//            JSONObject json = new JSONObject(payload);
//
//            Callback callback = new Callback();
////            callback.setClickTime(new Date());
////            callback.setClickid(json.optString("client_loan_id"));
////            callback.setProduct((Product) dao.get(Product.class, "productName='HDB'"));
////            callback.setCallbackLocation("/callback/web/Yubi_KYC_Status");
////            callback.setCallback(1);
////            callback.setCallbackContent(payload);
////            dao.saveOrUpdate(callback);
//            Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
//			if(optionalProduct.isEmpty()) {
//				throw new ProductNotFoundExeption("Product not found with name HDB");
//			}
//			callback.setProduct(optionalProduct.get());
//			callback.setApi("/callback/web/Yubi_KYC_Status");
//			callback.setContent(payload);
//			callback.setuID(json.optString("client_loan_id"));
//			
//			callbackRepository.save(callback);
//			
//			// Broadcast to frontend subscribers
//	        messagingTemplate.convertAndSend("/topic/callbacks", callback);
//            
//
//            // Extract status and message from the incoming payload
//            String status = json.optString("status", "Success");
//            String message = json.optString("message", "KYC webhook received successfully");
//
//            // Return JSON response
//            return "{ \"status\": \"Success\", \"message\": \"" + message + "\" }";
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return "{ \"status\": \"Error\", \"message\": \"Exception occurred while processing Yubi KYC Status Webhook\" }";
//        }
//    }
    
    @RequestMapping(value = "/callback/web/Yubi_KYC_Status", method = RequestMethod.POST)
    @ResponseBody
    public String Yubi_KYC_Webhook(@RequestBody String payload) {
        try {
            JSONObject json = new JSONObject(payload);

            Callback callback = new Callback();
            Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
            if(optionalProduct.isEmpty()) {
                throw new ProductNotFoundExeption("Product not found with name HDB");
            }

            callback.setProduct(optionalProduct.get());
            callback.setApi("/callback/web/Yubi_KYC_Status");
            callback.setContent(payload);
            callback.setuID(json.optString("client_loan_id"));

            callbackRepository.save(callback);

            // Broadcast to frontend
            messagingTemplate.convertAndSend("/topic/callbacks", callback);

            // Normalize status
            String rawStatus = json.optString("status", "FAILED");
            String normalizedStatus = rawStatus.equalsIgnoreCase("success") ? "Success" : "Failed";
            String message = json.optString("message", "KYC webhook received");

            // Optional: handle failed status
            if ("Failed".equals(normalizedStatus)) {
                System.err.println("❌ KYC failed for client_loan_id: " + json.optString("client_loan_id"));
            }

            return "{ \"status\": \"" + normalizedStatus + "\", \"message\": \"" + message + "\" }";

        } catch (Exception ex) {
            ex.printStackTrace();
            return "{ \"status\": \"Error\", \"message\": \"Exception occurred while processing Yubi KYC Status Webhook\" }";
        }
    }



    
    @RequestMapping(value="/callback/web/YubiBankAccount")
    @ResponseBody
    public String Yubi_Bank_Account(@RequestBody String payload){
    	
    	try {
			JSONObject json = new JSONObject(payload);
			Callback callback = new Callback();
			
//			   callback = new CallBack();
//			   callback.setClickTime(new Date());
//			   callback.setClickid(json.optString("client_loan_id"));
//			   callback.setProduct((Product)dao.get(Product.class,"productName='HDB'"));
//			   callback.setCallbackLocation("/callback/web/YubiBankAccount");
//			   callback.setCallback(1);
//			   callback.setCallbackContent(payload);
//			   dao.saveOrUpdate(callback);
			
			Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
			if(optionalProduct.isEmpty()) {
				throw new ProductNotFoundExeption("Product not found with name HDB");
			}
			callback.setProduct(optionalProduct.get());
			callback.setApi("/callback/web/YubiBankAccount");
			callback.setContent(payload);
			callback.setuID(json.optString("client_loan_id"));
			
			callbackRepository.save(callback);
			
			// Broadcast to frontend subscribers
	        messagingTemplate.convertAndSend("/topic/callbacks", callback);
		
		} catch (Exception ex) {}
    	return "ok";
    }
    
    
    @RequestMapping(value = "/callback/web/Yubi_Report_Status", method = RequestMethod.POST)
    @ResponseBody
    public String Yubi_Report_Status(@RequestBody String payload) {
        try {
            JSONObject json = new JSONObject(payload);

            Callback callback = new Callback();
//            callback.setClickTime(new Date());
//            callback.setClickid(json.optString("client_loan_id"));
//            callback.setProduct((Product) dao.get(Product.class, "productName='HDB'"));
//            callback.setCallbackLocation("/callback/web/Yubi_Report_Status");
//            callback.setCallback(1);
//            callback.setCallbackContent(payload);
//            dao.saveOrUpdate(callback);
            
            Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
			if(optionalProduct.isEmpty()) {
				throw new ProductNotFoundExeption("Product not found with name HDB");
			}
			callback.setProduct(optionalProduct.get());
			callback.setApi("/callback/web/Yubi_Report_Status");
			callback.setContent(payload);
			callback.setuID(json.optString("client_loan_id"));
			
			callbackRepository.save(callback);
			
			// Broadcast to frontend subscribers
	        messagingTemplate.convertAndSend("/topic/callbacks", callback);

            // Always return this fixed status and message like Yubi expects
            return "{ \"status\": \"Success\", \"message\": \"Account Aggregator Report Status Webhook request has received successfully\" }";

        } catch (Exception ex) {
            ex.printStackTrace();
            return "{ \"status\": \"Error\", \"message\": \"Exception occurred while processing Yubi Report Status Webhook\" }";
        }
    }

    
    
    @RequestMapping(value="/callback/web/Yubi_Document_Status")
    @ResponseBody
    public String Yub_Document_Status(@RequestBody String payload){
    	
    	try {
			JSONObject json = new JSONObject(payload);
			Callback callback = new Callback();
			
//			   callback = new CallBack();
//			   callback.setClickTime(new Date());
//			   callback.setClickid(json.optString("loan_id"));
//			   callback.setProduct((Product)dao.get(Product.class,"productName='HDB'"));
//			   callback.setCallbackLocation("/callback/web/Yubi_Document_Status");
//			   callback.setCallback(1);
//			   callback.setCallbackContent(payload);
//			   dao.saveOrUpdate(callback);
			Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
			if(optionalProduct.isEmpty()) {
				throw new ProductNotFoundExeption("Product not found with name HDB");
			}
			callback.setProduct(optionalProduct.get());
			callback.setApi("/callback/web/Yubi_Document_Status");
			callback.setContent(payload);
			callback.setuID(json.optString("loan_id"));
			
			callbackRepository.save(callback);
			
			// Broadcast to frontend subscribers
	        messagingTemplate.convertAndSend("/topic/callbacks", callback);
		
		} catch (Exception ex) {}
    	return "ok";
    }
    
    
    @RequestMapping(value="/callback/web/Yubi_AA_Redirection_URL_Webhook")
    @ResponseBody
    public String Yubi_AA_Redirection_URL_Webhook(@RequestBody String payload){
    	
    	try {
			JSONObject json = new JSONObject(payload);
			Callback callback = new Callback();
			
//			   callback = new CallBack();
//			   callback.setClickTime(new Date());
//			   callback.setClickid(json.optString("client_loan_id"));
//			   callback.setProduct((Product)dao.get(Product.class,"productName='HDB'"));
//			   callback.setCallbackLocation("/callback/web/Yubi_AA_Redirection_URL_Webhook");
//			   callback.setCallback(1);
//			   callback.setCallbackContent(payload);
//			   dao.saveOrUpdate(callback);
			Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
			if(optionalProduct.isEmpty()) {
				throw new ProductNotFoundExeption("Product not found with name HDB");
			}
			callback.setProduct(optionalProduct.get());
			callback.setApi("/callback/web/Yubi_AA_Redirection_URL_Webhook");
			callback.setContent(payload);
			callback.setuID(json.optString("client_loan_id"));
			
			callbackRepository.save(callback);
		
		} catch (Exception ex) {}
    	return "ok";
    }
    
    @RequestMapping(value = "/callback/web/Yubi_esign_status_Webhook", method = RequestMethod.POST)
    @ResponseBody
    public String Yubi_esign_status_Webhook(@RequestBody String payload) {
        try {
            JSONObject json = new JSONObject(payload);

            Callback callback = new Callback();
//            callback.setClickTime(new Date());
//            callback.setClickid(json.optString("client_loan_id"));
//            callback.setProduct((Product) dao.get(Product.class, "productName='HDB'"));
//            callback.setCallbackLocation("/callback/web/Yubi_esign_status_Webhook");
//            callback.setCallback(1);
//            callback.setCallbackContent(payload);
//            dao.saveOrUpdate(callback);
            
            Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
			if(optionalProduct.isEmpty()) {
				throw new ProductNotFoundExeption("Product not found with name HDB");
			}
			callback.setProduct(optionalProduct.get());
			callback.setApi("/callback/web/Yubi_esign_status_Webhook");
			callback.setContent(payload);
			callback.setuID(json.optString("client_loan_id"));
			
			callbackRepository.save(callback);
			
			messagingTemplate.convertAndSend("/topic/callbacks", callback);

            // Extract status and message from the payload or provide defaults
            String status = json.optString("status", "Success");
            String message = json.optString("message", "eSign Status Webhook received successfully");

            return "{ \"status\": \"" + status + "\", \"message\": \"" + message + "\" }";

        } catch (Exception ex) {
            ex.printStackTrace();
            return "{ \"status\": \"Error\", \"message\": \"Exception occurred while processing Yubi eSign Status Webhook\" }";
        }
    }

    
    @RequestMapping(value="/callback/web/Yubi_disbursement_status_Webhook")
    @ResponseBody
    public String Yubi_disbursement_status_Webhook(@RequestBody String payload){
    	
    	try {
			JSONObject json = new JSONObject(payload);
			Callback callback = new Callback();
			
//			   callback = new CallBack();
//			   callback.setClickTime(new Date());
//			   callback.setClickid(json.optString("client_loan_id"));
//			   callback.setProduct((Product)dao.get(Product.class,"productName='HDB'"));
//			   callback.setCallbackLocation("/callback/web/Yubi_disbursement_status_Webhook");
//			   callback.setCallback(1);
//			   callback.setCallbackContent(payload);
//			   dao.saveOrUpdate(callback);
			
			Optional<Product> optionalProduct = productRepository.findByProductName("HDB");
			if(optionalProduct.isEmpty()) {
				throw new ProductNotFoundExeption("Product not found with name HDB");
			}
			callback.setProduct(optionalProduct.get());
			callback.setApi("/callback/web/Yubi_disbursement_status_Webhook");
			callback.setContent(payload);
			callback.setuID(json.optString("client_loan_id"));
			
			callbackRepository.save(callback);
		
		} catch (Exception ex) {}
    	return "ok";
    }

}
