package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Transactions;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepositoryPort {
    Transactions createTransaccion(Transactions transaccion);
    List<Transactions> showTransacciones();
    Transactions showTransaccionById(int id);
    List<Transactions> showTransaccionesByUserId(Integer id);
    List<Transactions> showTransaccionesByPeriod(LocalDate fechaInicio, LocalDate fechaFin);
}
