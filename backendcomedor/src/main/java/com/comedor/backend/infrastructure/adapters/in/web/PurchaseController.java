package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ConfirmPurchaseUseCase;
import com.comedor.backend.application.ports.in.CreatePurchaseUseCase;
import com.comedor.backend.application.ports.in.GetPurchaseByIdUseCase;
import com.comedor.backend.application.ports.in.ListPurchaseUseCase;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final CreatePurchaseUseCase createPurchaseUseCase;
    private final ListPurchaseUseCase listPurchaseUseCase;
    private final ConfirmPurchaseUseCase confirmPurchaseUseCase;
    private final GetPurchaseByIdUseCase getPurchaseByIdUseCase;

    @PreAuthorize("hasAuthority('CREATE_ORDER_IN')")
    @PostMapping
    public PurchaseResponseDTO create(
            @RequestBody CreatePurchaseRequestDTO request
    ) {

        return createPurchaseUseCase.create(request);
    }

    @PreAuthorize("hasAuthority('PURCHASE_LIST_ALL')")
    @GetMapping
    public Page<PurchaseResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            StatusOrder status
    ) {
        return listPurchaseUseCase.list(
                page,
                size,
                startDate,
                endDate,
                status
        );
    }

    @PreAuthorize("hasAuthority('PURCHASE_CHANGE_STATUS')")
    @PatchMapping("/{id}/confirm")
    public PurchaseResponseDTO confirm(
            @PathVariable Integer id
    ) {
        return confirmPurchaseUseCase.confirm(id);
    }

    @GetMapping("/{id}")
    public PurchaseResponseDTO getById(@PathVariable Integer id) {
        return getPurchaseByIdUseCase.getById(id);
    }
}