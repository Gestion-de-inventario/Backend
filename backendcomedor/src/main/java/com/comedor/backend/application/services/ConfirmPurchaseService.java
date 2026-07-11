package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PurchaseMapper;
import com.comedor.backend.application.ports.in.ConfirmPurchaseUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.InventoryLotRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.PurchaseRepositoryPort;
import com.comedor.backend.domain.exceptions.DateException;
import com.comedor.backend.domain.model.InventoryLot;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Purchase;
import com.comedor.backend.domain.model.PurchaseDetail;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConfirmPurchaseService implements ConfirmPurchaseUseCase {

    private final PurchaseRepositoryPort purchaseRepository;
    private final ProductRepositoryPort productRepository;
    private final PurchaseMapper mapper;
    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final CurrentUserService currentUserService;
    private final InventoryLotRepositoryPort inventoryLotRepository;

    public ConfirmPurchaseService(PurchaseRepositoryPort purchaseRepository, ProductRepositoryPort productRepository, PurchaseMapper mapper, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService, InventoryLotRepositoryPort inventoryLotRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.currentUserService = currentUserService;
        this.inventoryLotRepository = inventoryLotRepository;
    }

    @Override
    public PurchaseResponseDTO confirm(Integer purchaseId) {
        Purchase purchase =
                purchaseRepository.findById(purchaseId);

        if(purchase.getPurchaseDate().isAfter(PeruTime.today()))
        {
            throw new DateException("Error al confirmar orden : No se puede marcar como recibido una orden de compra futura");
        }

        if (purchase.getStatus() == StatusOrder.RECIBIDO) {
            throw new RuntimeException(
                    "La orden ya fue confirmada"
            );
        }

        for (PurchaseDetail detail : purchase.getDetails()) {

            Product product =
                    productRepository.getProductoById(
                            detail.getProduct().getId()
                    );

            BigDecimal actualStock = product.getStock();

            product.setStock(
                    product.getStock()
                            .add(detail.getQuantity())
            );

            InventoryLot lot = new InventoryLot();

            lot.setProduct(detail.getProduct());

            lot.setQuantity(detail.getQuantity());

            lot.setRemainingQuantity(detail.getQuantity());

            lot.setUnitCost(detail.getUnitPrice());

            lot.setEntryDate(PeruTime.now());

            inventoryLotRepository.create(lot);

            detail.setInventoryLot(lot);

            Integer usuarioId =
                    currentUserService.getCurrentUser().getId();

            registrarMovimiento(
                    usuarioId,
                    product.getId(),
                    product.getName(),
                    detail.getQuantity(),
                    actualStock,
                    PeruTime.now()
            );

            productRepository.updateStock(product);

        }

        Purchase updated =
                purchaseRepository.updateStatus(purchaseId, StatusOrder.RECIBIDO);

        return mapper.toResponse(updated);
    }



    private void registrarMovimiento(
            Integer usuarioId,
            Integer referenceId,
            String itemName,
            BigDecimal amount,
            BigDecimal currentStock,
            LocalDateTime localDateTime
    ) {
        TransactionRequestDTO dto = new TransactionRequestDTO();

        dto.setReferenceType(TransactionReferenceType.INGREDIENTE);
        dto.setReferenceId(referenceId);
        dto.setItemName(itemName);
        dto.setType(MovementType.ENTRADA);
        dto.setAmount(amount);
        dto.setCurrentStock(currentStock);
        dto.setSource(TransactionSource.COMPRA);
        dto.setUserId(usuarioId);
        dto.setDateTime(localDateTime);

        registerTransactionUseCase.registrarTransaccion(dto);
    }

}




