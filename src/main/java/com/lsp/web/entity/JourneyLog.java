package com.lsp.web.entity;

import java.util.Date;

import com.lsp.web.genericentity.BaseEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "t_journey_log")
public class JourneyLog extends BaseEntity{

    // If you have an ApplyRecord entity, map it properly
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;  // FK to UserInfo table

    private Integer stage;

    private String UId;

    private String platformId;
    
    private String requestId;


	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public Integer getStage() {
		return stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public String getUId() {
		return UId;
	}

	public void setUId(String uId) {
		UId = uId;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}





    // Getters and setters...
}

