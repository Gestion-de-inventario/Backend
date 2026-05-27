package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.CreatePurchaseUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreatePurchaseRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PurchaseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final CreatePurchaseUseCase createPurchaseUseCase;

    @PreAuthorize("hasAuthority('PURCHASE_CREATE')")
    @PostMapping
    public PurchaseResponseDTO create(
            @RequestBody CreatePurchaseRequestDTO request
    ) {

        return createPurchaseUseCase.create(request);
    }
}