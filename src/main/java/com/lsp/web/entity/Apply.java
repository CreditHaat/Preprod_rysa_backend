//package com.lsp.web.entity;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//import com.lsp.web.genericentity.BaseEntity;
//
//@Entity
//@Table(name = "apply")
//public class Apply extends BaseEntity{
//
////    @OneToOne
////    @JoinColumn(name = "user_id", referencedColumnName = "id")
////    private UserInfo user;
//	@ManyToOne
//	@JoinColumn(name = "user_id", referencedColumnName = "id")
//	private UserInfo user;
//    
//    @ManyToOne
//    @JoinColumn(name = "journey_log_id")  // FK column in Apply table
//    private JourneyLog journeyLog;
//
//    public JourneyLog getJourneyLog() {
//		return journeyLog;
//	}
//
//	public void setJourneyLog(JourneyLog journeyLog) {
//		this.journeyLog = journeyLog;
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
//	public String getProductName() {
//		return productName;
//	}
//
//	public void setProductName(String productName) {
//		this.productName = productName;
//	}
//
//	public String getMobileNumber() {
//		return mobileNumber;
//	}
//
//	public void setMobileNumber(String mobileNumber) {
//		this.mobileNumber = mobileNumber;
//	}
//
//	public Integer getStage() {
//		return stage;
//	}
//
//	public void setStage(Integer stage) {
//		this.stage = stage;
//	}
//
//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}
//
//	private String productName;
//
//    private String mobileNumber;
//
//    private Integer stage;
//    
//    @Column(name = "url", length = 512)
//    private String url;
//    
//
//    // Getters and setters
//}

package com.lsp.web.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.lsp.web.genericentity.BaseEntity;

@Entity
@Table(name = "apply")
public class Apply extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo user;
    
    @ManyToOne
    @JoinColumn(name = "journey_log_id")  // FK column in Apply table
    private JourneyLog journeyLog;

    public JourneyLog getJourneyLog() {
		return journeyLog;
	}

	public void setJourneyLog(JourneyLog journeyLog) {
		this.journeyLog = journeyLog;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getStage() {
		return stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String productName;

    private String mobileNumber;

    private Integer stage;
    
    @Column(name = "url", length = 512)
    private String url;
    

    // Getters and setters
}

