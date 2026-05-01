package com.comedor.backend.domain.model;

import java.time.LocalDateTime;

public class Modificaciones {
    private int id;
    private Usuario user;
    private String editedClass;
    private String editedAttribute;
    private String  previousValue;
    private String newValue;
    private LocalDateTime dateTime;
}