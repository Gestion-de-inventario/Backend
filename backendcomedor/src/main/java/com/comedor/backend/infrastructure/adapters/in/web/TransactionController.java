package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ListarTransaccionesUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final ListarTransaccionesUseCase listarTransaccionesUseCase;

    @PreAuthorize("hasAuthority('TRANSACTION_LIST_ALL')")
    @GetMapping
    public Page<TransaccionResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return listarTransaccionesUseCase.list(page, size);
    }

}
