package com.lsp.web.dto;

public class ForeclosureUpdateRequestDTO {
	
	 private String transactionId;
	    private String bppId;
	    private String bppUri;
	    private String orderId;
	    private String paymentLabel; // should be "FORECLOSURE"
	    private String version;
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
		public String getPaymentLabel() {
			return paymentLabel;
		}
		public void setPaymentLabel(String paymentLabel) {
			this.paymentLabel = paymentLabel;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}

}
