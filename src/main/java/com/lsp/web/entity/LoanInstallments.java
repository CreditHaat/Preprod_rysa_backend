package com.lsp.web.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.lsp.web.genericentity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_loan_installments")
public class LoanInstallments extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "disbursed_loan_id", nullable = false)
	private DisbursedLoan disbursedLoan;
	
	private String installmentId;
	private String type;
	private LocalDate startDate;
	private LocalDate endDate;
	private String status;
	private BigDecimal installmentAmount;
	public DisbursedLoan getDisbursedLoan() {
		return disbursedLoan;
	}
	public void setDisbursedLoan(DisbursedLoan disbursedLoan) {
		this.disbursedLoan = disbursedLoan;
	}
	public String getInstallmentId() {
		return installmentId;
	}
	public void setInstallmentId(String installmentId) {
		this.installmentId = installmentId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getInstallmentAmount() {
		return installmentAmount;
	}
	public void setInstallmentAmount(BigDecimal installmentAmount) {
		this.installmentAmount = installmentAmount;
	}

}
