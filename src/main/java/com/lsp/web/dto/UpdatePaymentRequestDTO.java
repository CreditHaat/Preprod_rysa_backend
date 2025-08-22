package com.lsp.web.dto;

public class UpdatePaymentRequestDTO {

    private String transactionId;
    private String bppId;
    private String bppUri;
    private String orderId;
    private String amount;
    private String currency;
    private String version;

    // Getters and Setters
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
