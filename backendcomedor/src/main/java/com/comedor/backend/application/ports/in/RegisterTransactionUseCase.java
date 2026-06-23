package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransactionResponseDTO;

public interface RegisterTransactionUseCase {

    TransactionResponseDTO registrarTransaccion(TransactionRequestDTO transaccion);

}
