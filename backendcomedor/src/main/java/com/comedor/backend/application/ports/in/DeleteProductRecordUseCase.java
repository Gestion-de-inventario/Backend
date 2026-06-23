package com.comedor.backend.application.ports.in;

public interface DeleteProductRecordUseCase {
    void eliminarRegistroProducto(
            int reporteId,
            int registroId
    );
}
