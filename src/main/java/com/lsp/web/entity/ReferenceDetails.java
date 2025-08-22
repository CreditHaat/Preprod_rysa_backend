package com.lsp.web.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.lsp.web.genericentity.BaseEntity;

@Entity
@Table(name = "reference_details")
public class ReferenceDetails extends BaseEntity{
	
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String relationship;

    @Column(nullable = false)
    private String address;

    private String clientLoanId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getClientLoanId() {
		return clientLoanId;
	}

	public void setClientLoanId(String clientLoanId) {
		this.clientLoanId = clientLoanId;
	}

    // Getters and setters
    
    
}
