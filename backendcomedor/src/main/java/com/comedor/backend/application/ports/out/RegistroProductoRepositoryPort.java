package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Registro;

import java.util.List;

public interface RegistroProductoRepositoryPort {
    Registro agregarRegistroProducto(int reporteId,Registro registro);
    Registro actualizarRegistroProducto(int reporteId, int registroId,Registro registro);
    void eliminarRegistroProducto(int reporteId,int registroId);
    List<Registro> findByReporteId(int reporteId);
    Registro findById(int id);
}
