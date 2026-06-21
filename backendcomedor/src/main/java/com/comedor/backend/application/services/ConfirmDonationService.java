package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DonationMapper;
import com.comedor.backend.application.ports.in.ConfirmDonationUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.DonationRepositoryPort;
import com.comedor.backend.application.ports.out.InventoryLotRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.*;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.domain.model.enums.FuenteTransaccion;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;

import java.math.BigDecimal;

public class ConfirmDonationService implements ConfirmDonationUseCase {
    private final DonationRepositoryPort repository;
    private final DonationMapper mapper;
    private final ProductRepositoryPort productRepository;
    private final RegistrarTransaccionUseCase registrarTransaccionUseCase;
    private final CurrentUserService currentUserService;
    private final InventoryLotRepositoryPort inventoryLotRepository;

    public ConfirmDonationService(DonationRepositoryPort repository, DonationMapper mapper, ProductRepositoryPort productRepository, RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService, InventoryLotRepositoryPort inventoryLotRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.productRepository = productRepository;
        this.registrarTransaccionUseCase = registrarTransaccionUseCase;
        this.currentUserService = currentUserService;
        this.inventoryLotRepository = inventoryLotRepository;
    }

    @Override
    public DonationResponseDTO confirm(Integer donationId) {

        Donation donation = repository.findById(donationId);

        if (donation.getStatus() == EstadoOrden.RECIBIDO) {
            throw new RuntimeException(
                    "La orden ya fue confirmada"
            );
        }

        for (DonationDetail detail : donation.getDetails()) {

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

            lot.setUnitCost(BigDecimal.ZERO);

            lot.setEntryDate(PeruTime.now());

            inventoryLotRepository.create(lot);

            detail.setInventoryLot(lot);

            Integer usuarioId =
                    currentUserService.getCurrentUser().getId();

            registrarMovimiento(
                    usuarioId,
                    product.getId(),
                    detail.getQuantity(),
                    TipoMovimiento.ENTRADA,
                    FuenteTransaccion.DONACION
            );

            productRepository.updateStock(product);

        }
        Donation updatedDonation = repository.changeStatus(donationId, EstadoOrden.RECIBIDO);

        return mapper.toResponse(updatedDonation);
    }


    private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            TipoMovimiento tipo,
            FuenteTransaccion source
    ) {
        TransaccionRequestDTO dto = new TransaccionRequestDTO();

        dto.setAmount(amount);
        dto.setProductId(productoId);
        dto.setUserId(usuarioId);
        dto.setType(tipo);
        dto.setSource(source);

        registrarTransaccionUseCase.registrarTransaccion(dto);
    }
}
