package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.OrderInRepositoryPort;
import com.comedor.backend.domain.model.OrderIn;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.domain.model.enums.FuenteProducto;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.OrderInViewEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.OrderInEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.OrderInJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification.OrderInSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Component
@RequiredArgsConstructor
public class OrderInRepositoryAdapter implements OrderInRepositoryPort {
    private final OrderInJpaRepository orderInJpaRepository;
    private final OrderInEntityMapper orderInEntityMapper;

    @Override
    public Page<OrderIn> showOrderIns(
            LocalDate startDate,
            LocalDate endDate,
            FuenteProducto source,
            EstadoOrden status,
            Pageable pageable
    ) {
        Specification<OrderInViewEntity> spec =
                (root, query, cb) -> cb.conjunction();

        if (startDate != null) {
            spec = spec.and(
                    OrderInSpecification.dateAfter(startDate)
            );
        }

        if (endDate != null) {
            spec = spec.and(
                    OrderInSpecification.dateBefore(endDate)
            );
        }

        if (source != null) {
            spec = spec.and(
                    OrderInSpecification.hasSource(source)
            );
        }

        if (status != null) {
            spec = spec.and(
                    OrderInSpecification.hasStatus(status)
            );
        }

        return orderInJpaRepository.findAll(spec, pageable).map(orderInEntityMapper::toDomain);
    }
}
