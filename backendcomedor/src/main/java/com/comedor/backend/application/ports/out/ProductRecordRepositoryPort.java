package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Record;

import java.util.List;

public interface ProductRecordRepositoryPort {
    Record agregarRegistroProducto(int reporteId, Record record);
    Record actualizarRegistroProducto(int reporteId, int registroId, Record record);
    void eliminarRegistroProducto(int reporteId,int registroId);
    List<Record> findByReporteId(int reporteId);
    Record findById(int id);
}
