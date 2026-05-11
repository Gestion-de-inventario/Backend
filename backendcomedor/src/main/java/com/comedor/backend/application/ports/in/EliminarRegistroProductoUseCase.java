package com.comedor.backend.application.ports.in;

public interface EliminarRegistroProductoUseCase {
    void eliminarRegistroProducto(
            int reporteId,
            int registroId
    );
}
