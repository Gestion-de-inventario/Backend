package com.comedor.backend.domain.model;

import java.time.LocalDateTime;

public class Modifications {
    private int id;
    private User user;
    private String editedClass;
    private String editedAttribute;
    private String  previousValue;
    private String newValue;
    private LocalDateTime dateTime;

    public Modifications(LocalDateTime dateTime, String newValue, String previousValue, String editedAttribute, String editedClass, User user, int id) {
        this.dateTime = dateTime;
        this.newValue = newValue;
        this.previousValue = previousValue;
        this.editedAttribute = editedAttribute;
        this.editedClass = editedClass;
        this.user = user;
        this.id = id;
    }

    public Modifications() {
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getEditedClass() {
        return editedClass;
    }

    public String getEditedAttribute() {
        return editedAttribute;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEditedClass(String editedClass) {
        this.editedClass = editedClass;
    }

    public void setEditedAttribute(String editedAttribute) {
        this.editedAttribute = editedAttribute;
    }

    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}