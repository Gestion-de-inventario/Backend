package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PurchaseMapper;
import com.comedor.backend.application.ports.in.ConfirmPurchaseUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.InventoryLotRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.PurchaseDetailRepositoryPort;
import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.model.InventoryLot;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConfirmPurchaseService implements ConfirmPurchaseUseCase {

    private final PurchaseRepositoryPort purchaseRepository;
    private final ProductRepositoryPort productRepository;
    private final PurchaseMapper mapper;
    private final RegistrarTransaccionUseCase registrarTransaccionUseCase;
    private final CurrentUserService currentUserService;
    private final InventoryLotRepositoryPort inventoryLotRepository;

    public ConfirmPurchaseService(PurchaseRepositoryPort purchaseRepository, ProductRepositoryPort productRepository, PurchaseMapper mapper, RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService, InventoryLotRepositoryPort inventoryLotRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.registrarTransaccionUseCase = registrarTransaccionUseCase;
        this.currentUserService = currentUserService;
        this.inventoryLotRepository = inventoryLotRepository;
    }

    @Override
    public PurchaseResponseDTO confirm(Integer purchaseId) {
        Purchase purchase =
                purchaseRepository.findById(purchaseId);

        if (purchase.getStatus() == EstadoOrden.RECIBIDO) {
            throw new RuntimeException(
                    "La orden ya fue confirmada"
            );
        }

        for (PurchaseDetail detail : purchase.getDetails()) {

            Product product =
                    productRepository.getProductoById(
                            detail.getProduct().getId()
                    );

            product.setStock(
                    product.getStock()
                            .add(detail.getQuantity())
            );

            InventoryLot lot = new InventoryLot();

            lot.setProduct(detail.getProduct());

            lot.setQuantity(detail.getQuantity());

            lot.setRemainingQuantity(detail.getQuantity());

            lot.setUnitCost(detail.getUnitPrice());

            lot.setEntryDate(LocalDateTime.now());

            inventoryLotRepository.create(lot);

            detail.setInventoryLot(lot);

            Integer usuarioId =
                    currentUserService.getCurrentUser().getId();

            registrarMovimiento(
                    usuarioId,
                    product.getId(),
                    detail.getQuantity(),
                    TipoMovimiento.ENTRADA
            );

            productRepository.updateStock(product);

        }

        Purchase updated =
                purchaseRepository.updateStatus(purchaseId,EstadoOrden.RECIBIDO);

        return mapper.toResponse(updated);
    }



    private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            TipoMovimiento tipo
    ) {
        TransaccionRequestDTO dto = new TransaccionRequestDTO();

        dto.setAmount(amount);
        dto.setProductId(productoId);
        dto.setUserId(usuarioId);
        dto.setType(tipo);

        registrarTransaccionUseCase.registrarTransaccion(dto);
    }

}


