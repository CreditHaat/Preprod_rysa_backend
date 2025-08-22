package com.lsp.web.entity;

import com.lsp.web.genericentity.BaseEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "master_bank")
public class MasterBank extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String bankName;

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

    // Getters and Setters
    
    
}
