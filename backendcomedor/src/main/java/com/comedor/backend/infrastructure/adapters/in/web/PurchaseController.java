package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ConfirmPurchaseUseCase;
import com.comedor.backend.application.ports.in.CreatePurchaseUseCase;
import com.comedor.backend.application.ports.in.ListPurchaseUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final CreatePurchaseUseCase createPurchaseUseCase;
    private final ListPurchaseUseCase listPurchaseUseCase;
    private final ConfirmPurchaseUseCase confirmPurchaseUseCase;
    @PreAuthorize("hasAuthority('PURCHASE_CREATE')")
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
            @RequestParam(defaultValue = "20") int size
    ) {
        return listPurchaseUseCase.list(page, size);
    }

    @PreAuthorize("hasAuthority('PURCHASE_CHANGE_STATUS')")
    @PatchMapping("/{id}/confirm")
    public PurchaseResponseDTO confirm(
            @PathVariable Integer id
    ) {
        return confirmPurchaseUseCase.confirm(id);
    }
}