package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

public class Beneficiario {
    private int id;
    private String dni;
    private String name;
    private String lastname;
    private Estado estado = Estado.ACTIVO;
}
