package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransactionResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ListTransactionsUseCase {
    Page<TransactionResponseDTO> list(int page, int size,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      MovementType type,
                                      TransactionSource source,
                                      String productName);
}
