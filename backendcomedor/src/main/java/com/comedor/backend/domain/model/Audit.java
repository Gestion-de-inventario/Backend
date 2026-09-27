package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.AuditAction;

import java.time.LocalDateTime;

import java.util.Map;

public class Audit {

    private Integer id;
    private User user;

    private String entityType;
    private Integer entityId;
    private String entityName;

    private AuditAction action;

    private Map<String, Object> details;

    private LocalDateTime dateTime;

    public Audit() {
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getEntityType() {
        return entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public AuditAction getAction() {
        return action;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Audit{" +
                "id=" + id +
                ", user=" + user.toString() +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", entityName='" + entityName + '\'' +
                ", action=" + action +
                ", details=" + details +
                ", dateTime=" + dateTime +
                '}';
    }
}