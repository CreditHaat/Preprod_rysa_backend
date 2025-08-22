package com.lsp.web.dto;

public class UpdateRequestDTO {
    private String transactionId;
    private String bppId;
    private String bppUri;
    private String orderId;
    private String fulfillmentState;
    
    private String version;
    
    //Generate Getters and Setters
    
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getBppId() {
		return bppId;
	}
	public void setBppId(String bppId) {
		this.bppId = bppId;
	}
	public String getBppUri() {
		return bppUri;
	}
	public void setBppUri(String bppUri) {
		this.bppUri = bppUri;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getFulfillmentState() {
		return fulfillmentState;
	}
	public void setFulfillmentState(String fulfillmentState) {
		this.fulfillmentState = fulfillmentState;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
    
}

