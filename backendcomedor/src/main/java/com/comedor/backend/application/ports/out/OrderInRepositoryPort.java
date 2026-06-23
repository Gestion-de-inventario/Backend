package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.OrderIn;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.domain.model.enums.ProductSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderInRepositoryPort {
    Page<OrderIn> showOrderIns(
            LocalDate startDate,
            LocalDate endDate,
            ProductSource source,
            StatusOrder status,
            Pageable pageable
    );
}
