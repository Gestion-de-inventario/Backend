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
}