package com.lsp.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.lsp.web.entity.DisbursedLoan;

public class LoanInstallmentDTO {
    private String id;   // 👈 force primary key to string
    private String installmentId;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private BigDecimal installmentAmount;
    private DisbursedLoan disbursedLoan;  // 👈 keep full object

    // Getters & Setters
    public String getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id != null ? id.toString() : null;
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

    public DisbursedLoan getDisbursedLoan() {
        return disbursedLoan;
    }
    public void setDisbursedLoan(DisbursedLoan disbursedLoan) {
        this.disbursedLoan = disbursedLoan;
    }
}
