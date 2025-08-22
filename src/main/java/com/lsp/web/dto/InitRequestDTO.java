package com.lsp.web.dto;

public class InitRequestDTO {

	private String transactionId;
	private String bppId;
	private String bppUri;
	private String providerId;
	private String itemId;
	private String formId;
	private String submissionId;
	private String bankCode;
	private String accountNumber;
	private String vpa;
	private String settlementAmount;
	
	private String mobileNumber;
    private Integer stage;
    private String productName;//this is used to save the product name in apply record
    
    //bank details form Data
    private String formType;//this will be 1. Bank Details 2. Emandate 3. Loan Aggrement
    private String accountname;
    private String accountType;
    private String IFSC;
//    private String accountNumber;
    
    private String version;
    private Integer initAttempt;
    private String paymentId;
	
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
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(String submissionId) {
		this.submissionId = submissionId;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getVpa() {
		return vpa;
	}
	public void setVpa(String vpa) {
		this.vpa = vpa;
	}
	public String getSettlementAmount() {
		return settlementAmount;
	}
	public void setSettlementAmount(String settlementAmount) {
		this.settlementAmount = settlementAmount;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public Integer getStage() {
		return stage;
	}
	public void setStage(Integer stage) {
		this.stage = stage;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getAccountname() {
		return accountname;
	}
	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getIFSC() {
		return IFSC;
	}
	public void setIFSC(String iFSC) {
		IFSC = iFSC;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Integer getInitAttempt() {
		return initAttempt;
	}
	public void setInitAttempt(Integer initAttempt) {
		this.initAttempt = initAttempt;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
}
