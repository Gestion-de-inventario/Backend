package com.comedor.backend.infrastructure.adapters.in.web;


import com.comedor.backend.application.ports.in.CrearProductoUseCase;
import com.comedor.backend.application.ports.in.ListarProductosPorEstadoUseCase;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final CrearProductoUseCase crearProductoUseCase;
    private final ListarProductosPorEstadoUseCase listarProductosPorEstadoUseCase;

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @GetMapping("/listar")
    public List<ProductoResponseDTO> listarProductos(@RequestParam(required = false) Estado estado)
    {
        return listarProductosPorEstadoUseCase.listarProductosPorEstado(estado);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA')")
    @PostMapping("/create")
    public ProductoResponseDTO crearProducto(@RequestBody ProductoRequestDTO productoRequestDTO)
    {
        return crearProductoUseCase.crearProducto(productoRequestDTO);
    }

}
