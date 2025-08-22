//package com.lsp.web.entity;
//
//import java.time.LocalDateTime;
//
//
//import com.lsp.web.genericentity.BaseEntity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//
//@Entity
////@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
//@Table(name = "t_repayment")
//public class Repayment extends BaseEntity {
//	
//	private String emiAmount;
//	private LocalDateTime dueDate;
//	private String remainingAmount;
//	private String amountToPay;
//	
//	public String getEmiAmount() {
//		return emiAmount;
//	}
//	public void setEmiAmount(String emiAmount) {
//		this.emiAmount = emiAmount;
//	}
//	public LocalDateTime getDueDate() {
//		return dueDate;
//	}
//	public void setDueDate(LocalDateTime dueDate) {
//		this.dueDate = dueDate;
//	}
//	public String getRemainingAmount() {
//		return remainingAmount;
//	}
//	public void setRemainingAmount(String remainingAmount) {
//		this.remainingAmount = remainingAmount;
//	}
//	public String getAmountToPay() {
//		return amountToPay;
//	}
//	public void setAmountToPay(String amountToPay) {
//		this.amountToPay = amountToPay;
//	}
//	
//}

package com.lsp.web.entity;

import java.time.LocalDateTime;

import com.lsp.web.genericentity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_repayment")
public class Repayment extends BaseEntity {

    private String transactionId;
    private String paymentId;
    private String emiAmount;
    private LocalDateTime dueDate;
    private String remainingAmount;
    private String amountToPay;

    // getters & setters
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public String getEmiAmount() {
        return emiAmount;
    }
    public void setEmiAmount(String emiAmount) {
        this.emiAmount = emiAmount;
    }
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    public String getRemainingAmount() {
        return remainingAmount;
    }
    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    public String getAmountToPay() {
        return amountToPay;
    }
    public void setAmountToPay(String amountToPay) {
        this.amountToPay = amountToPay;
    }
}

