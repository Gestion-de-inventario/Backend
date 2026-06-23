package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ListOrderInsUseCase;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.domain.model.enums.ProductSource;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.OrderInResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/orders-in")
@RequiredArgsConstructor
public class OrderInController {
    private final ListOrderInsUseCase listOrderInsUseCase;


    @PreAuthorize("hasAuthority('ORDER_IN_LIST_ALL')")
    @GetMapping
    public Page<OrderInResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            ProductSource source,

            @RequestParam(required = false)
            StatusOrder status
    ) {
        return listOrderInsUseCase.list(
                page,
                size,
                startDate,
                endDate,
                source,
                status
        );
    }
}
