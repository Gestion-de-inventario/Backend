package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PurchaseMapper;
import com.comedor.backend.application.ports.in.CreatePurchaseUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseDetailRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreatePurchaseService implements CreatePurchaseUseCase {

    private final PurchaseRepositoryPort purchaseRepository;

    private final ProductRepositoryPort productRepository;

    private final PurchaseMapper purchaseMapper;

    public CreatePurchaseService(PurchaseRepositoryPort purchaseRepository,
                                 ProductRepositoryPort productRepository,
                                 PurchaseMapper purchaseMapper) {

        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.purchaseMapper = purchaseMapper;
    }

    @Override
    public PurchaseResponseDTO create(CreatePurchaseRequestDTO request) {

        List<PurchaseDetail> details = new ArrayList<>();

        BigDecimal totalSpent = BigDecimal.ZERO;

        for (CreatePurchaseDetailRequestDTO item : request.getDetails()) {

            Product product = productRepository
                    .getProductoById(item.getProductId());

            BigDecimal subtotal =
                    item.getQuantity()
                            .multiply(item.getUnitPrice());

            PurchaseDetail detail = new PurchaseDetail();

            detail.setProduct(product);

            detail.setQuantity(item.getQuantity());

            detail.setUnitPrice(item.getUnitPrice());

            detail.setSubTotal(subtotal);

            details.add(detail);

            totalSpent = totalSpent.add(subtotal);
        }

        Purchase purchase = new Purchase();

        purchase.setPurchaseDate(PeruTime.today());

        purchase.setStatus(EstadoOrden.PENDIENTE);

        purchase.setTotalSpent(totalSpent);

        purchase.setDetails(details);

        details.forEach(detail -> detail.setPurchase(purchase));

        Purchase savedPurchase =
                purchaseRepository.save(purchase);

        return purchaseMapper.toResponse(savedPurchase);
    }
}