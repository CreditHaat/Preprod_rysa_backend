package com.lsp.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lsp.web.genericentity.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "t_disbursed_loan")
public class DisbursedLoan extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserInfo user;  // FK to UserInfo

    @Column(nullable = false)
    private String loanNumber;//this loan number refers to the order id of the loan

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private BigDecimal outstandingAmount;

    private BigDecimal interestRate;

    private Integer tenureMonths;

//    private LocalDate startDate;
//
//    private LocalDate endDate;
    
    private String type;

    private String status;
    
    private String transactionId;
    
    private String bppId;
    private String bppUri;
    
    private String version;

    // Getters and Setters
    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
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
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public void setVersion(String version) {
		this.version = version;
	}
	
}
