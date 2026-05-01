package com.comedor.backend.domain.model;
import com.comedor.backend.domain.model.enums.Estado;

public class Subcategoria {
    private Integer id;
    private String name;
    private Estado status = Estado.ACTIVO;
}
