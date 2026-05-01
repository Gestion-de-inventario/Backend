package com.comedor.backend.domain.model;

import java.time.LocalDateTime;

public class Modificaciones {
    private int id;
    private Usuario user;
    private String edited_class;
    private String edited_attribute;
    private String  previous_value;
    private String new_value;
    private LocalDateTime date_time;
}