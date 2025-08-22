//package com.lsp.web.entity;
//
//import jakarta.persistence.*;
//import com.lsp.web.genericentity.BaseEntity;
//
//@Entity
//@Table(name = "apply_fail")
//public class ApplyFail extends BaseEntity {
//
//    @Column(name = "product_name")
//    private String productName;
//
//    @Column(name = "user_mobile_number", length = 10)
//    private String userMobileNumber;
//
//    // Foreign key to UserInfo
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private UserInfo user;
//
//    // Foreign key to JourneyLog (apply_record)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "journey_log_id", nullable = false)
//    private JourneyLog journeyLog;
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "apply_id") // <- New field for easier reference
//    private Apply apply;
//
//    // Getters and Setters
//
//    public Apply getApply() {
//		return apply;
//	}
//
//	public void setApply(Apply apply) {
//		this.apply = apply;
//	}
//
//	public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getUserMobileNumber() {
//        return userMobileNumber;
//    }
//
//    public void setUserMobileNumber(String userMobileNumber) {
//        this.userMobileNumber = userMobileNumber;
//    }
//
//    public UserInfo getUser() {
//        return user;
//    }
//
//    public void setUser(UserInfo user) {
//        this.user = user;
//    }
//
//	public JourneyLog getJourneyLog() {
//		return journeyLog;
//	}
//
//	public void setJourneyLog(JourneyLog journeyLog) {
//		this.journeyLog = journeyLog;
//	}
//
//}

package com.lsp.web.entity;

import jakarta.persistence.*;
import com.lsp.web.genericentity.BaseEntity;

@Entity
@Table(name = "apply_fail")
public class ApplyFail extends BaseEntity {

    @Column(name = "product_name")
    private String productName;

    @Column(name = "user_mobile_number", length = 10)
    private String userMobileNumber;

    // Foreign key to UserInfo
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo user;

    // Foreign key to JourneyLog (apply_record)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_log_id", nullable = false)
    private JourneyLog journeyLog;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_id") // <- New field for easier reference
    private Apply apply;

    // Getters and Setters

    public Apply getApply() {
		return apply;
	}

	public void setApply(Apply apply) {
		this.apply = apply;
	}

	public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

	public JourneyLog getJourneyLog() {
		return journeyLog;
	}

	public void setJourneyLog(JourneyLog journeyLog) {
		this.journeyLog = journeyLog;
	}

}


