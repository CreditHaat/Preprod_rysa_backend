package com.lsp.web.entity;

import com.lsp.web.genericentity.BaseEntity;
import com.lsp.web.util.StringUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
//@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@Table(name = "t_product")
public class Product extends BaseEntity {
    @Column(name = "logo", length = 200)
    private String logo;

    @Column(name = "product_name", length = 50,nullable=false)
    private String productName;

    @Column(name = "amount_range", length = 50)
    private String amountRange;

    @Column(name = "min_amount", columnDefinition="INT NULL")
    private Integer minAmount;

    @Column(name = "max_amount", columnDefinition="INT NULL")
    private Integer maxAmount;

    @Column(name = "interest_range", length = 50)
    private String interestRange;

    @Column(name = "min_interest", columnDefinition="decimal(20,2) NULL")
    private Float minInterest;

    @Column(name = "max_interest", columnDefinition="decimal(20,2) NULL")
    private Float maxInterest;

    @Column(name = "tenure_range", length = 50)
    private String tenureRange;

    @Column(name = "min_tenure", columnDefinition="INT NULL")
    private Integer minTenure;

    @Column(name = "max_tenure", columnDefinition="INT NULL")
    private Integer maxTenure;

    @Column(name = "min_salary", columnDefinition="INT NULL")
    private Integer minSalary;
    
    @Column(name = "max_salary", columnDefinition="INT NULL")
    private Integer maxSalary;

    @Column(name = "min_experian", columnDefinition="INT NULL")
    private Integer minExperian;

    @Column(name = "url", length = 300)
    private String url;

    @Column(name = "cpi", columnDefinition="INT NOT NULL default 0")
    private Integer cpi;

    @Column(name = "only_salary", columnDefinition="INT NOT NULL default 0")
    private Integer onlySalary;

    @Column(name = "only_netpay", columnDefinition="INT NOT NULL default 0")
    private Integer onlyNetpay;

    @Column(name = "disburse_time", length = 50)
    private String disburseTime;

    @Column(name = "features", length = 500)
    private String features;

    @Column(name = "short_desc", length = 100)
    private String shortDesc;

    @Column(name = "process", length = 500)
    private String process;

    @Column(name = "sample", length = 500)
    private String sample;

    @Column(name = "fees", length = 500)
    private String fees;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "others", length = 500)
    private String others;

    @Column(name = "status", columnDefinition="INT default 0")
    private Integer status;

    @Column(name = "supported", length = 50)
    private String supported;

    @Column(name = "sortindex", columnDefinition="INT default 0")
    private Integer sortindex;

    @Column(name = "sortcredithaat", columnDefinition="INT default 0")
    private Integer sortcredithaat;

    @Column(name = "archived", columnDefinition="INT default 0")
    private Integer archived;
    
    @Column(name = "excludeDsa",length = 2000)
    private String excludeDsa;
    
    @Column(name = "applink", length = 2000)
    private String applink; 
    
    @Column(name = "rejectionmsg", length = 300)
    private String rejectionmsg;
    
    @Column(name = "max_loanamount", length = 300)
    private String max_loanamount;
    
    @Column(name = "min_loanamount", length = 300)
    private String min_loanamount;
    
    @Column(name = "other", columnDefinition="INT NOT NULL default 0")
    private Integer other;
    
    @Column(name = "bronze", columnDefinition="INT NOT NULL default 0")
    private Integer bronze;
    
    @Column(name = "silver", columnDefinition="INT NOT NULL default 0")
    private Integer silver;
    
    @Column(name = "gold", columnDefinition="INT NOT NULL default 0")
    private Integer gold;
    
    @Column(name = "platinum", columnDefinition="INT NOT NULL default 0")
    private Integer platinum;
    
    @Column(name = "credit_profile", columnDefinition="INT NOT NULL default 0")
    private Integer credit_profile;
    
    @Column(name = "recurring", columnDefinition="INT NOT NULL default 0")
    private Integer recurring;
    
    @Column(name = "min_age", columnDefinition="INT default 0")
    private Integer min_age;
    
    @Column(name = "max_age", columnDefinition="INT default 0")
    private Integer max_age;
    
    @Column(name = "applink_redirection", length = 2000)
    private String applink_redirection;
    
    @Column(name = "short_applink_redirection", length = 2000)
    private String short_applink_redirection;

    public String[] getFeaturesList(){
        if(StringUtil.nullOrEmpty(this.features))
            return null;
        return this.features.split(",");
    }

    public String toString(){
        return this.productName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortindex() {
        return sortindex;
    }

    public void setSortindex(Integer sortindex) {
        this.sortindex = sortindex;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAmountRange() {
        return amountRange;
    }

    public void setAmountRange(String amountRange) {
        this.amountRange = amountRange;
    }

    public String getInterestRange() {
        return interestRange;
    }

    public void setInterestRange(String interestRange) {
        this.interestRange = interestRange;
    }

    public String getTenureRange() {
        return tenureRange;
    }

    public void setTenureRange(String tenureRange) {
        this.tenureRange = tenureRange;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getDisburseTime() {
        return disburseTime;
    }

    public void setDisburseTime(String disburseTime) {
        this.disburseTime = disburseTime;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Float getMinInterest() {
        return minInterest;
    }

    public void setMinInterest(Float minInterest) {
        this.minInterest = minInterest;
    }

    public Float getMaxInterest() {
        return maxInterest;
    }

    public void setMaxInterest(Float maxInterest) {
        this.maxInterest = maxInterest;
    }

    public Integer getMinTenure() {
        return minTenure;
    }

    public void setMinTenure(Integer minTenure) {
        this.minTenure = minTenure;
    }

    public Integer getMaxTenure() {
        return maxTenure;
    }

    public void setMaxTenure(Integer maxTenure) {
        this.maxTenure = maxTenure;
    }

    public Integer getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public Integer getMinExperian() {
        return minExperian;
    }

    public void setMinExperian(Integer minExperian) {
        this.minExperian = minExperian;
    }

    public Integer getCpi() {
        return cpi;
    }

    public void setCpi(Integer cpi) {
        this.cpi = cpi;
    }

    public Integer getOnlySalary() {
        return onlySalary;
    }

    public void setOnlySalary(Integer onlySalary) {
        this.onlySalary = onlySalary;
    }

    public String getSupported() {
        return supported;
    }

    public void setSupported(String supported) {
        this.supported = supported;
    }

    public Integer getSortcredithaat() {
        return sortcredithaat;
    }

    public void setSortcredithaat(Integer sortcredithaat) {
        this.sortcredithaat = sortcredithaat;
    }

    public Integer getOnlyNetpay() {
        return onlyNetpay;
    }

    public void setOnlyNetpay(Integer onlyNetpay) {
        this.onlyNetpay = onlyNetpay;
    }

    public Integer getArchived() {
        return archived;
    }

    public void setArchived(Integer archived) {
        this.archived = archived;
    }

	public String[] getExcludeDsaList() {
		if(StringUtil.nullOrEmpty(this.excludeDsa))
            return null;
        return this.excludeDsa.split(",");
	}

	public void setExcludeDsa(String excludeDsa) {
		this.excludeDsa = excludeDsa;
	}

	public String getExcludeDsa() {
		return excludeDsa;
	}
	
    
    public Integer getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }
    
    public String getApplink() {
        return applink;
    }

    public void setApplink(String applink) {
        this.applink = applink;
    }
    
    public String getRejectionmsg() {
        return rejectionmsg;
    }

    public void setRejectionmsg(String rejectionmsg) {
        this.rejectionmsg = rejectionmsg;
    }
    
    public String getMax_loanamount() {
        return max_loanamount;
    }

    public void setMax_loanamount(String max_loanamount) {
        this.max_loanamount = max_loanamount;
    }
    
    public String getMin_loanamount() {
        return min_loanamount;
    }

    public void setMin_loanamount(String min_loanamount) {
        this.min_loanamount = min_loanamount;
    }
    
    public Integer getOther() {
        return other;
    }

    public void setOther(Integer other) {
        this.other = other;
    }
    
    public Integer getBronze() {
        return bronze;
    }

    public void setBronze(Integer bronze) {
        this.bronze = bronze;
    }
    
    public Integer getSilver() {
        return silver;
    }

    public void setSilver(Integer silver) {
        this.silver = silver;
    }
    
    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }
    
    public Integer getPlatinum() {
        return platinum;
    }

    public void setPlatinum(Integer platinum) {
        this.platinum = platinum;
    }
    
    public Integer getCredit_profile() {
        return credit_profile;
    }

    public void setCredit_profile(Integer credit_profile) {
        this.credit_profile = credit_profile;
    }
    
    public Integer getRecurring() {
        return recurring;
    }

    public void setRecurring(Integer recurring) {
        this.recurring = recurring;
    }
    
    public Integer getMin_age() {
        return min_age;
    }

    public void setMin_age(Integer min_age) {
        this.min_age = min_age;
    }
    
    public Integer getMax_age() {
        return max_age;
    }

    public void setMax_age(Integer max_age) {
        this.max_age = max_age;
    }

	public String getApplink_redirection() {
		return applink_redirection;
	}

	public void setApplink_redirection(String applink_redirection) {
		this.applink_redirection = applink_redirection;
	}

	public String getShort_applink_redirection() {
		return short_applink_redirection;
	}

	public void setShort_applink_redirection(String short_applink_redirection) {
		this.short_applink_redirection = short_applink_redirection;
	}
}

