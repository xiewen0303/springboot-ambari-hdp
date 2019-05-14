package com.mongo.bean;


import org.bson.types.ObjectId;
import java.sql.Timestamp;

public class Account {

    public Account() {
    }

    private ObjectId id;

    private Long userRoleId;

    private Long reYb;

    private Long noReYb;

    private Long jb;

    private Long bindYb;

    private Long updateTime;

    private Timestamp createTime;

    private Integer userType;

    private String userId;

    private String serverId;


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Long userRoleId) {
        this.userRoleId = userRoleId;
    }

    public Long getReYb() {
        return reYb;
    }

    public void setReYb(Long reYb) {
        this.reYb = reYb;
    }

    public Long getNoReYb() {
        return noReYb;
    }

    public void setNoReYb(Long noReYb) {
        this.noReYb = noReYb;
    }

    public Long getJb() {
        return jb;
    }

    public void setJb(Long jb) {
        this.jb = jb;
    }

    public Long getBindYb() {
        return bindYb;
    }

    public void setBindYb(Long bindYb) {
        this.bindYb = bindYb;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
