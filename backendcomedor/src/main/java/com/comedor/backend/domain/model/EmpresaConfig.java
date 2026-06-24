package com.comedor.backend.domain.model;

public class EmpresaConfig {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String logoBase64;

    public EmpresaConfig(){return;}

    public EmpresaConfig(Integer id, String nombre, String descripcion, String logoBase64) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.logoBase64 = logoBase64;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLogoBase64() {
        return logoBase64;
    }

    public void setLogoBase64(String logoBase64) {
        this.logoBase64 = logoBase64;
    }
}
