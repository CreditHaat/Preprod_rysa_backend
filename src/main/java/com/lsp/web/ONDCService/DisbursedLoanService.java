package com.lsp.web.ONDCService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsp.web.Exception.LoanNotFoundException;
import com.lsp.web.dto.DisbursedLoanDTO2;
import com.lsp.web.dto.LoanMapper;
import com.lsp.web.entity.DisbursedLoan;
import com.lsp.web.entity.LoanInstallments;
import com.lsp.web.entity.UserInfo;
import com.lsp.web.repository.DisbursedLoanRepository;
import com.lsp.web.repository.LoanInstallmentsRepository;
import com.lsp.web.repository.UserInfoRepository;

import jakarta.transaction.Transactional;

@Service
public class DisbursedLoanService {

	@Autowired
	private DisbursedLoanRepository loanRepository;

	@Autowired
	private UserInfoRepository userRepository;
	
	@Autowired
	private LoanInstallmentsRepository loanInstallmentsRepository;

	public void saveLoanFromOnConfirm(String requestBody) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode onConfirmJson = mapper.readTree(requestBody);

		// Detect version from context or structure
		String version = onConfirmJson.path("context").path("version").asText();
		
		String transactionId = onConfirmJson.path("context").path("transaction_id").asText();
		String bppId = onConfirmJson.path("context").path("bpp_id").asText();
		String bppUri = onConfirmJson.path("context").path("bpp_uri").asText();

		// Common JSON pointers for both versions
		JsonNode order = onConfirmJson.path("message").path("order");
		if (order.isMissingNode()) {
			throw new IllegalArgumentException("Invalid on_confirm payload: missing order");
		}

		// Loan creation
		DisbursedLoan loan = new DisbursedLoan();
		
		loan.setTransactionId(transactionId);
		loan.setBppId(bppId);
		loan.setBppUri(bppUri);
		loan.setVersion(version);

		// Find User by mobile/email from fulfillments or customer
		String mobile = order.path("fulfillments").path(0).path("customer").path("contact").path("phone").asText();
		UserInfo user = userRepository.findByMobileNumber(mobile)
				.orElseThrow(() -> new RuntimeException("User not found for mobile: " + mobile));
		loan.setUser(user);

		// Loan Number
		String loanNumber = order.path("id").asText();
		loan.setLoanNumber(loanNumber);

		// Principal Amount (in 2.0.0 sometimes in quotes, sometimes numeric)
		BigDecimal principal;
		try {
			principal = new BigDecimal(order.path("items").path(0).path("price").path("value").asText()); 
		}catch(Exception e) {
			principal = new BigDecimal(0);
			e.printStackTrace();
		}
		
		loan.setPrincipalAmount(principal);

		// Outstanding Amount (may be same as principal initially)
		BigDecimal outstanding = new BigDecimal(order.path("items").path(0).path("price").path("value").asText());
		loan.setOutstandingAmount(outstanding);

		// Interest Rate (look for tags in 2.0.0 or payment/breakup in 2.0.1)
//        String interestRate = null;
//        String tenure = null;
		JsonNode tagsNode = order.path("items").path(0).path("tags");
//        if (tagsNode.isArray()) {
//            for (JsonNode tag : tagsNode) {
//            	if("INTEREST_RATE".equalsIgnoreCase(tag.path("list").path(0).path("descriptor").path("code").asText())) {
//            		interestRate = tag.path("list").path(0).path("descriptor").path("value").asText();
////                    break;
//            	}
//            	// Tenure Months
//            	if ("TERM".equalsIgnoreCase(tag.path("list").path(1).path("descriptor").path("code").asText())) {
//                    tenure = tag.path("list").path(1).path("descriptor").path("value").asText();
//                    break;
//                }
//            }
//        }

		BigDecimal interestRate = null;
		Integer tenure = null;

		if (tagsNode.isArray()) {
			for (JsonNode tag : tagsNode) {
				String outerCode = tag.path("descriptor").path("code").asText();
				if ("LOAN_INFO".equalsIgnoreCase(outerCode)) {
					JsonNode loanInfoList = tag.path("list");
					if (loanInfoList.isArray()) {
						for (JsonNode infoItem : loanInfoList) {
							String code = infoItem.path("descriptor").path("code").asText();
							String value = infoItem.path("value").asText();

							if ("INTEREST_RATE".equalsIgnoreCase(code)) {
								value = value.replace("%", "").trim();
								try {
									interestRate = new BigDecimal(value);
								} catch (NumberFormatException e) {
									System.err.println("Invalid interest rate: " + value);
								}
							}

							if ("TERM".equalsIgnoreCase(code)) {
								// Extract digits from something like "5 months"
								value = value.replaceAll("[^0-9]", "").trim();
								try {
									tenure = Integer.parseInt(value);
								} catch (NumberFormatException e) {
									System.err.println("Invalid loan term: " + value);
								}
							}
						}
					}
				}
			}
		}
		loan.setInterestRate(interestRate);

		// Tenure Months
//        Integer tenure = null;
//        if (tagsNode.isArray()) {
//            for (JsonNode tag : tagsNode) {
//                if ("tenure_months".equalsIgnoreCase(tag.path("code").asText())) {
//                    tenure = tag.path("value").asInt();
//                    break;
//                }
//            }
//        }
		loan.setTenureMonths(tenure);

		// Status
		loan.setStatus(order.path("state").path("descriptor").path("code").asText("PENDING"));

		// Save Loan
		loanRepository.save(loan);

		JsonNode paymentsNode = order.path("payments");
		if (paymentsNode.isArray()) {
			for (JsonNode paymentNode : paymentsNode) {
				if ("POST_FULFILLMENT".equalsIgnoreCase(paymentNode.path("type").asText())) {

//					String startDate = paymentNode.path("time").path("range").path("start").asText();
//					String endDate = paymentNode.path("time").path("range").path("end").asText();
					
					String startDateStr = paymentNode.path("time").path("range").path("start").asText();
					String endDateStr = paymentNode.path("time").path("range").path("end").asText();

					LocalDate startDate = null;
					LocalDate endDate = null;

					if (startDateStr != null && !startDateStr.isEmpty()) {
					    startDate = LocalDate.parse(startDateStr.substring(0, 10));  // "2025-09-01"
					}

					if (endDateStr != null && !endDateStr.isEmpty()) {
					    endDate = LocalDate.parse(endDateStr.substring(0, 10));  // "2025-09-30"
					}

					String intsallmentAmount = paymentNode.path("params").path("amount").asText();

					LoanInstallments loanInstallments = new LoanInstallments();
					loanInstallments.setStartDate(startDate);
					loanInstallments.setEndDate(endDate);
					loanInstallments.setInstallmentId(paymentNode.path("id").asText());
					loanInstallments.setInstallmentAmount(new BigDecimal(intsallmentAmount));
					loanInstallments.setType(paymentNode.path("type").asText());
					loanInstallments.setStatus(paymentNode.path("status").asText());
					loanInstallments.setDisbursedLoan(loan);

					loanInstallmentsRepository.save(loanInstallments);

				}
			}
		}

	}

//	public ResponseEntity<?> fetchLoans(String mobileNumber) {
//		try {
//			UserInfo userInfo = new UserInfo();
//			UserInfo user = userRepository.findByMobileNumber(mobileNumber)
//					.orElseThrow(() -> new RuntimeException("User not found for mobile: " + mobileNumber));
//			List<DisbursedLoan> disbursedLoanList = loanRepository.findByUser(user);
////			Map()
//			
//			return ResponseEntity.ok(disbursedLoanList);
//			
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public ResponseEntity<?> fetchLoans(String mobileNumber) {
	    try {
	        UserInfo user = userRepository.findByMobileNumber(mobileNumber)
	                .orElseThrow(() -> new RuntimeException("User not found for mobile: " + mobileNumber));

	        List<DisbursedLoan> disbursedLoanList = loanRepository.findByUser(user);

	        // Convert to DTO list
	        List<DisbursedLoanDTO2> dtoList = disbursedLoanList.stream()
	                .map(loan -> {
	                    DisbursedLoanDTO2 dto = new DisbursedLoanDTO2();
	                    dto.setId(String.valueOf(loan.getId()));   // ✅ primary key
	                    dto.setLoanNumber(loan.getLoanNumber());
	                    dto.setPrincipalAmount(loan.getPrincipalAmount());
	                    dto.setOutstandingAmount(loan.getOutstandingAmount());
	                    dto.setInterestRate(loan.getInterestRate());
	                    dto.setTenureMonths(loan.getTenureMonths());
	                    dto.setType(loan.getType());
	                    dto.setStatus(loan.getStatus());
	                    dto.setTransactionId(loan.getTransactionId());
	                    dto.setBppId(loan.getBppId());
	                    dto.setBppUri(loan.getBppUri());
	                    dto.setVersion(loan.getVersion());
	                    dto.setCreateTime(loan.getCreateTime());
	                    return dto;
	                })
	                .collect(Collectors.toList());

	        return ResponseEntity.ok(dtoList);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", e.getMessage()));
	    }
	}
	
//	public List<DisbursedLoanDTO2> fetchLoansByMobile(String mobileNumber) {
//	    List<DisbursedLoan> loans = DisbursedLoanRepository.findByUserMobile(mobileNumber);
//	    return loans.stream()
//	                .map(LoanMapper::toDTO)
//	                .collect(Collectors.toList());
//	}

	@Transactional
	public void updateLoanFromOnUpdate(String requestBody) throws JsonProcessingException {
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode onUpdateJson = mapper.readTree(requestBody);

	    // Context
	    String transactionId = onUpdateJson.path("context").path("transaction_id").asText();
	    String bppId = onUpdateJson.path("context").path("bpp_id").asText();
	    String bppUri = onUpdateJson.path("context").path("bpp_uri").asText();
	    String version = onUpdateJson.path("context").path("version").asText();

	    JsonNode order = onUpdateJson.path("message").path("order");
	    if (order.isMissingNode()) {
	        throw new IllegalArgumentException("Invalid on_update payload: missing order");
	    }

	    // Fetch existing loan by transactionId
//	    Optional<DisbursedLoan> loan = loanRepository.findByTransactionId(transactionId)
//	            .orElseThrow(() -> new RuntimeException("Loan not found for transactionId: " + transactionId));
	    
	    Optional<DisbursedLoan> tempLoan = loanRepository.findByTransactionId(transactionId);
	    if(tempLoan.isEmpty()) {
	    	throw new LoanNotFoundException("Loan not found for transactionId: " + transactionId);
	    }
	    DisbursedLoan loan = tempLoan.get();
	    // --- Update Loan fields ---
	    loan.setBppId(bppId);
	    loan.setBppUri(bppUri);
	    loan.setVersion(version);

	    // Update Loan Number if present
	    String loanNumber = order.path("id").asText(null);
	    if (loanNumber != null && !loanNumber.isEmpty()) {
	        loan.setLoanNumber(loanNumber);
	    }

	    // Update Principal & Outstanding Amount from quote/breakup
	    BigDecimal principal = null;
	    BigDecimal outstanding = null;
	    
	    principal = new BigDecimal(order.path("items").path(0).path("price").path("value").asText());
	    
	    JsonNode breakup = order.path("quote").path("breakup");
	    if (breakup.isArray()) {
	        for (JsonNode item : breakup) {
	            String title = item.path("title").asText();
//	            if ("PRINCIPAL".equalsIgnoreCase(title)) {
//	                principal = new BigDecimal(item.path("price").path("value").asText("0"));
//	            }
	            if ("OUTSTANDING_PRINCIPAL".equalsIgnoreCase(title)) {
	                outstanding = new BigDecimal(item.path("price").path("value").asText("0"));
	            }
	        }
	    }
	    if (principal != null) loan.setPrincipalAmount(principal);
	    if (outstanding != null) loan.setOutstandingAmount(outstanding);

	    // Update Interest Rate & Tenure from tags
	    JsonNode tagsNode = order.path("items").path(0).path("tags");
	    BigDecimal interestRate = loan.getInterestRate();
	    Integer tenure = loan.getTenureMonths();

	    if (tagsNode.isArray()) {
	        for (JsonNode tag : tagsNode) {
	            String outerCode = tag.path("descriptor").path("code").asText();
	            if ("LOAN_INFO".equalsIgnoreCase(outerCode)) {
	                for (JsonNode infoItem : tag.path("list")) {
	                    String code = infoItem.path("descriptor").path("code").asText();
	                    String value = infoItem.path("value").asText();

	                    if ("INTEREST_RATE".equalsIgnoreCase(code)) {
	                        value = value.replace("%", "").trim();
	                        try {
	                            interestRate = new BigDecimal(value);
	                        } catch (NumberFormatException ignored) {}
	                    }
	                    if ("TERM".equalsIgnoreCase(code)) {
	                        value = value.replaceAll("[^0-9]", "").trim();
	                        try {
	                            tenure = Integer.parseInt(value);
	                        } catch (NumberFormatException ignored) {}
	                    }
	                }
	            }
	        }
	    }
	    loan.setInterestRate(interestRate);
	    loan.setTenureMonths(tenure);

	    // Update Status
	    String stateCode = order.path("fulfillments").path(0).path("state").path("descriptor").path("code").asText();
	    if (stateCode != null && !stateCode.isEmpty()) {
	        loan.setStatus(stateCode);
	    }
	    
	    // --- Insert new installments ---
	    JsonNode paymentsNode1 = order.path("payments");
	    BigDecimal remainingAmount = null;
//	    if (paymentsNode1.isArray()) {
//	        for (JsonNode paymentNode : paymentsNode1) {
//	        	
////	        	if("PAID".equalsIgnoreCase(paymentNode.path("status").asText())) {
////	        		
////	        		remainingAmount = principal.subtract(new BigDecimal(paymentNode.path("params").path("amount").asText()));
////	        	}
//	        	
//	            
//	        }
//	    }
	    
	    
//	    loan.setOutstandingAmount(remainingAmount);

	    loanRepository.save(loan);

	    // --- Delete old installments ---
	    loanInstallmentsRepository.deleteByDisbursedLoan(loan);

	    // --- Insert new installments ---
	    JsonNode paymentsNode = order.path("payments");
	    if (paymentsNode.isArray()) {
	        for (JsonNode paymentNode : paymentsNode) {
	            if ("POST_FULFILLMENT".equalsIgnoreCase(paymentNode.path("type").asText())) {
	                String id = paymentNode.path("id").asText(); // UUID string
	                String amountStr = paymentNode.path("params").path("amount").asText("0");
	                String status = paymentNode.path("status").asText();

	                LocalDate startDate = null, endDate = null;
	                String startDateStr = paymentNode.path("time").path("range").path("start").asText(null);
	                String endDateStr = paymentNode.path("time").path("range").path("end").asText(null);
	                if (startDateStr != null) startDate = LocalDate.parse(startDateStr.substring(0, 10));
	                if (endDateStr != null) endDate = LocalDate.parse(endDateStr.substring(0, 10));

	                LoanInstallments installment = new LoanInstallments();
	                installment.setDisbursedLoan(loan);
	                installment.setInstallmentId(id); // <-- now String
	                installment.setInstallmentAmount(new BigDecimal(amountStr));
	                installment.setStatus(status);
	                installment.setType(paymentNode.path("type").asText());
	                installment.setStartDate(startDate);
	                installment.setEndDate(endDate);

	                loanInstallmentsRepository.save(installment);
	            }
	        }
	    }
	}
	
	private BigDecimal safeBigDecimal(JsonNode node, String defaultValue) {
	    String value = node.asText(defaultValue);
	    try {
	        return new BigDecimal(value.trim());
	    } catch (NumberFormatException e) {
	        System.err.println("Invalid BigDecimal value: " + value);
	        return new BigDecimal(defaultValue);
	    }
	}
}
