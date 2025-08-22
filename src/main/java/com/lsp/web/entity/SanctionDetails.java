package com.lsp.web.entity;

import com.lsp.web.genericentity.BaseEntity;

import jakarta.persistence.*;

@Entity
public class SanctionDetails extends BaseEntity {
 

    public String getClientLoanId() {
		return clientLoanId;
	}

	public void setClientLoanId(String clientLoanId) {
		this.clientLoanId = clientLoanId;
	}

	public Float getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(Float loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Integer getTenure() {
		return tenure;
	}

	public void setTenure(Integer tenure) {
		this.tenure = tenure;
	}

	public Float getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Float interestRate) {
		this.interestRate = interestRate;
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	private String clientLoanId;
    private Float loanAmount;
    private Integer tenure;
    private Float interestRate;

    @OneToOne
    private Callback callback;

    // getters and setters
}
