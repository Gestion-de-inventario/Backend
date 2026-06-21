package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ConfirmDonationUseCase;
import com.comedor.backend.application.ports.in.CreateDonationUseCase;
import com.comedor.backend.application.ports.in.GetDonationByIdUseCase;
import com.comedor.backend.application.ports.in.ListDonationUseCase;
import com.comedor.backend.domain.model.enums.EstadoOrden;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDonationRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {
    private final CreateDonationUseCase createDonationUseCase;
    private final ListDonationUseCase listDonationUseCase;
    private final ConfirmDonationUseCase confirmDonationUseCase;
    private final GetDonationByIdUseCase getDonationByIdUseCase;

    @PreAuthorize("hasAuthority('CREATE_ORDER_IN')")
    @PostMapping
    public DonationResponseDTO create(
            @RequestBody CreateDonationRequestDTO request
    ) {

        return createDonationUseCase.create(request);
    }

    @PreAuthorize("hasAuthority('DONATION_LIST_ALL')")
    @GetMapping
    public Page<DonationResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            EstadoOrden status
    ) {
        return listDonationUseCase.list(
                page,
                size,
                startDate,
                endDate,
                status
        );
    }
    @PreAuthorize("hasAuthority('DONATION_CHANGE_STATUS')")
    @PatchMapping("/{id}/confirm")
    public DonationResponseDTO confirm(
            @PathVariable Integer id
    ) {
        return confirmDonationUseCase.confirm(id);
    }

    @GetMapping("/{id}")
    public DonationResponseDTO getById(@PathVariable Integer id) {
        return getDonationByIdUseCase.getById(id);
    }

}
