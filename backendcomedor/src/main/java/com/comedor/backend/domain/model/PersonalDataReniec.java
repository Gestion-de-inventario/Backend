package com.comedor.backend.domain.model;

public class PersonalDataReniec {
    private String dni;
    private String names;
    private String lastnames;

    public PersonalDataReniec(String dni, String names, String lastnames) {
        this.dni = dni;
        this.names = names;
        this.lastnames = lastnames;
    }

    public String getDni() { return dni; }
    public String getNames() { return names; }
    public String getLastnames() { return lastnames; }
}
