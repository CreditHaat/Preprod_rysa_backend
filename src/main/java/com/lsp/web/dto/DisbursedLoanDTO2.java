package com.lsp.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DisbursedLoanDTO2 {

    private String id;  // ✅ Primary Key
    private String loanNumber; // order id of the loan
    private BigDecimal principalAmount;
    private BigDecimal outstandingAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private String type;
    private String status;
    private String transactionId;
    private String bppId;
    private String bppUri;
    private String version;
    private LocalDateTime createTime;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getVersion() {
        return version;
    }

	public void setCreateTime(LocalDateTime localDateTime) {
		this.createTime = localDateTime;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
