package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Transactions;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TransactionRepositoryPort {
    Transactions createTransaccion(Transactions transaccion);
    Page<Transactions> showTransacciones(Pageable pageable);
    Transactions showTransaccionById(int id);
    List<Transactions> showTransaccionesByUserId(Integer id);
    List<Transactions> showTransaccionesByPeriod(LocalDate fechaInicio, LocalDate fechaFin);
}
