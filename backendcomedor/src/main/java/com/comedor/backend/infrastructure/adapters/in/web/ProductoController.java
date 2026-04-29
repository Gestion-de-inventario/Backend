package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.CrearProductoUseCase;

public class ProductoController {

    private final CrearProductoUseCase crearProductoUseCase;

    public ProductoController(CrearProductoUseCase crearProductoUseCase) {
        this.crearProductoUseCase = crearProductoUseCase;
    }
}
