//package com.lsp.web.genericentity;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Id;
//import jakarta.persistence.MappedSuperclass;
//import jakarta.persistence.PrePersist;
//import jakarta.persistence.PreUpdate;
//
//@MappedSuperclass
//public abstract class BaseEntity implements Serializable {
//
//    @Id
//    @Column(name = "id", nullable = false, updatable = false, length = 36)
//    private String id;
//
//    @Column(name = "createtime", updatable = false, nullable = false)
//    private LocalDateTime createTime;
//
//    @Column(name = "updatetime")
//    private LocalDateTime updateTime;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public LocalDateTime getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(LocalDateTime createTime) {
//        this.createTime = createTime;
//    }
//
//    public LocalDateTime getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(LocalDateTime updateTime) {
//        this.updateTime = updateTime;
//    }
//
//    // Lifecycle Callback Methods
//    @PrePersist
//    protected void onCreate() {
//        if (this.id == null) {
//            this.id = UUID.randomUUID().toString(); // generate UUID
//        }
//        this.createTime = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.updateTime = LocalDateTime.now();
//    }
//}

package com.lsp.web.genericentity;

import com.lsp.web.genericentity.SnowflakeIdGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id; // Use Long

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = idGenerator.nextId(); // Already returns Long
        }
        this.createTime = LocalDateTime.now();
    }

    @Column(name = "createtime", updatable = false, nullable = false)
    private LocalDateTime createTime;

    @Column(name = "updatetime")
    private LocalDateTime updateTime;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public static SnowflakeIdGenerator getIdgenerator() {
		return idGenerator;
	}

	public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

//    @PrePersist
//    protected void onCreate() {
//        if (this.id == null) {
//            this.id = String.valueOf(idGenerator.nextId()); // Use Snowflake
//        }
//        this.createTime = LocalDateTime.now();
//    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}

