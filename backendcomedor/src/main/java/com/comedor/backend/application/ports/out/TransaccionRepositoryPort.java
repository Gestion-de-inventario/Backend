package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Transacciones;

import java.time.LocalDate;
import java.util.List;

public interface TransaccionRepositoryPort {
    Transacciones createTransaccion(Transacciones transaccion);
    List<Transacciones> showTransacciones();
    Transacciones showTransaccionById(int id);
    List<Transacciones> showTransaccionesByUserId(Integer id);
    List<Transacciones> showTransaccionesByPeriod(LocalDate fechaInicio, LocalDate fechaFin);
}
