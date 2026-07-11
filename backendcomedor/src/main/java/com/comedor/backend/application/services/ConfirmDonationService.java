package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DonationMapper;
import com.comedor.backend.application.ports.in.ConfirmDonationUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.DonationRepositoryPort;
import com.comedor.backend.application.ports.out.InventoryLotRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.DateException;
import com.comedor.backend.domain.model.*;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConfirmDonationService implements ConfirmDonationUseCase {
    private final DonationRepositoryPort repository;
    private final DonationMapper mapper;
    private final ProductRepositoryPort productRepository;
    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final CurrentUserService currentUserService;
    private final InventoryLotRepositoryPort inventoryLotRepository;

    public ConfirmDonationService(DonationRepositoryPort repository, DonationMapper mapper, ProductRepositoryPort productRepository, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService, InventoryLotRepositoryPort inventoryLotRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.productRepository = productRepository;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.currentUserService = currentUserService;
        this.inventoryLotRepository = inventoryLotRepository;
    }

    @Override
    public DonationResponseDTO confirm(Integer donationId) {

        Donation donation = repository.findById(donationId);

        if(donation.getDonationDate().isAfter(PeruTime.today()))
        {
            throw new DateException("Error al confirmar orden : No se puede marcar como recibido una orden de donacion futura");
        }

        if (donation.getStatus() == StatusOrder.RECIBIDO) {
            throw new RuntimeException(
                    "La orden ya fue confirmada"
            );
        }

        for (DonationDetail detail : donation.getDetails()) {

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

            lot.setUnitCost(BigDecimal.ZERO);

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
        Donation updatedDonation = repository.changeStatus(donationId, StatusOrder.RECIBIDO);

        return mapper.toResponse(updatedDonation);
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
        dto.setSource(TransactionSource.DONACION);
        dto.setUserId(usuarioId);
        dto.setDateTime(localDateTime);

        registerTransactionUseCase.registrarTransaccion(dto);
    }
}
