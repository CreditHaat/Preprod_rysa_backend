//package com.lsp.web.entity;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//import com.lsp.web.genericentity.BaseEntity;
//
//@Entity
//@Table(name = "t_bankdetails")
//public class BankDetails extends BaseEntity {
//
//    private String bankName;
//    private String accountHolderName;
//    
//    @Column(length = 11)
//    private String ifsc;
//    private String accountNumber;
//    private String branchName;
//    public String getBankName() {
//		return bankName;
//	}
//
//	public void setBankName(String bankName) {
//		this.bankName = bankName;
//	}
//
//	public String getAccountHolderName() {
//		return accountHolderName;
//	}
//
//	public void setAccountHolderName(String accountHolderName) {
//		this.accountHolderName = accountHolderName;
//	}
//
//	public String getIfsc() {
//		return ifsc;
//	}
//
//	public void setIfsc(String ifsc) {
//		this.ifsc = ifsc;
//	}
//
//	public String getAccountNumber() {
//		return accountNumber;
//	}
//
//	public void setAccountNumber(String accountNumber) {
//		this.accountNumber = accountNumber;
//	}
//
//	public String getBranchName() {
//		return branchName;
//	}
//
//	public void setBranchName(String branchName) {
//		this.branchName = branchName;
//	}
//
//	public String getSelfieUrl() {
//		return selfieUrl;
//	}
//
//	public void setSelfieUrl(String selfieUrl) {
//		this.selfieUrl = selfieUrl;
//	}
//
//	public UserInfo getUser() {
//		return user;
//	}
//
//	public void setUser(UserInfo user) {
//		this.user = user;
//	}
//
//	@Column(length = 500)
//	private String selfieUrl;
//
//    // Relationship with UserInfo
//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private UserInfo user;
//    
//    private String accountType; // created by tejas
//	public String getAccountType() {
//		return accountType;
//	}
//
//	public void setAccountType(String accountType) {
//		this.accountType = accountType;
//	}
//
//
//    // Getters and Setters
//    // (Generate using IDE or Lombok)
//}

package com.lsp.web.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.lsp.web.genericentity.BaseEntity;

@Entity
@Table(name = "t_bankdetails")
public class BankDetails extends BaseEntity {

    private String bankName;
    private String accountHolderName;
    
    @Column(length = 11)
    private String ifsc;
    private String accountNumber;
    private String branchName;
    public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSelfieUrl() {
		return selfieUrl;
	}

	public void setSelfieUrl(String selfieUrl) {
		this.selfieUrl = selfieUrl;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	@Column(length = 500)
	private String selfieUrl;

    // Relationship with UserInfo
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo user;
    
    private String accountType; // created by tejas
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}


    // Getters and Setters
    // (Generate using IDE or Lombok)
}
