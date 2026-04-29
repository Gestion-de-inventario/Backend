package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.CrearProductoUseCase;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.domain.model.Producto;

public class ProductoService implements CrearProductoUseCase {
    private final ProductoRepositoryPort repositorio;
    public ProductoService(ProductoRepositoryPort repositorio){
        this.repositorio= repositorio;
    }

    @Override
    public void crearProducto(Producto producto) {
        repositorio.guardar(producto);
    }
}
