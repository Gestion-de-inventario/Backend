package com.comedor.backend.infrastructure.adapters.out.persistence.repository;

import com.comedor.backend.infrastructure.adapters.out.persistence.entity.OrderInViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderInJpaRepository  extends JpaRepository<OrderInViewEntity,String>,
        JpaSpecificationExecutor<OrderInViewEntity> {
}
