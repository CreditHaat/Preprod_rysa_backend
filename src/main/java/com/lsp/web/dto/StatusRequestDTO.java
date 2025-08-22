package com.lsp.web.dto;

public class StatusRequestDTO {
    private String transactionId;
    private String bppId;
    private String bppUri;
    private String refId;
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

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}