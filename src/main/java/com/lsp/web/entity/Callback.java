package com.lsp.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lsp.web.genericentity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import jakarta.persistence.Index;


//@Entity
//@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
//@Table(name = "t_callback")
@Entity
@Table(name = "t_callback", indexes = {
    @Index(name = "idx_uid", columnList = "uID"),
    @Index(name = "idx_apiid", columnList = "apiId"),
    @Index(name = "idx_product_id", columnList = "product_id")
})
public class Callback extends BaseEntity {
	
    @ManyToOne(targetEntity = Product.class,cascade={CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name="product_id")
    @JsonIgnore
    private Product product;

    //we will store the transaction id and other apis unique id into this uid
    private String uID;
    
    //we will store the message id for ondc into this
    private String apiId;

    @Column(name = "callback_content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "callback_api", length=500)
    private String api;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getuID() {
		return uID;
	}

	public void setuID(String uID) {
		this.uID = uID;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}
    
}

