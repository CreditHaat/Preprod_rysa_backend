package com.lsp.web.dto;

import java.util.Map;

public class FormLogRequest {
	
	private String mobileNumber;
    private String transactionId;
//    private Map<String, Object> request;  // your formSubmissionData
    private ONDCFormDataDTO ondcFormDataDTO;
    private Object response;
    private String gatewayUrl;
    private String formSubmissionStatus;
    private String productName;
    
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public ONDCFormDataDTO getOndcFormDataDTO() {
		return ondcFormDataDTO;
	}
	public void setOndcFormDataDTO(ONDCFormDataDTO ondcFormDataDTO) {
		this.ondcFormDataDTO = ondcFormDataDTO;
	}
	public Object getResponse() {
		return response;
	}
	public void setResponse(Object response) {
		this.response = response;
	}
	public String getGatewayUrl() {
		return gatewayUrl;
	}
	public void setGatewayUrl(String gatewayUrl) {
		this.gatewayUrl = gatewayUrl;
	}
	public String getFormSubmissionStatus() {
		return formSubmissionStatus;
	}
	public void setFormSubmissionStatus(String formSubmissionStatus) {
		this.formSubmissionStatus = formSubmissionStatus;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}

}
