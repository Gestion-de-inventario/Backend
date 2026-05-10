package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Registro;

public interface RegistroProductoRepositoryPort {
    Registro agregarRegistroProducto(int reporteId,Registro registro);
    Registro actualizarRegistroProducto(int reporteId, int registroId,Registro registro);
    Registro eliminarRegistroProducto(int reporteId,int registroId);
}
