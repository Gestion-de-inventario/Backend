package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditarProductoUseCase;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductoConTransaccionesException;
import com.comedor.backend.domain.exceptions.ProductoYaExisteException;
import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarProductoRequestDTO;

public class EditarProductoService implements EditarProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public EditarProductoService (ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Override
    public Producto editar(int id, EditarProductoRequestDTO request) {
        Producto producto = productoRepositoryPort.getProductoById(id);
        boolean tieneTransacciones = productoRepositoryPort.tieneTransaccionesVinculadas(id);

        if (tieneTransacciones) {
            boolean intentaCambiarCamposBloqueados =
                    (request.getName() != null && !request.getName().equalsIgnoreCase(producto.getName())) ||
                            (request.getCategoria() != null) ||
                            (request.getEtiqueta() != null) ||
                            (request.getUnit() != null);

            if (intentaCambiarCamposBloqueados) {
                throw new ProductoConTransaccionesException(
                        "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden"
                );
            }

            if (request.getReorderPoint() != null) producto.setReorderPoint(request.getReorderPoint());
            return productoRepositoryPort.updateProducto(producto);
        }

        if (request.getName() != null && !request.getName().equalsIgnoreCase(producto.getName())) {
            if (productoRepositoryPort.existByNameAndIdNot(request.getName(), id)) {
                throw new ProductoYaExisteException("Ya existe un producto con el nombre: " + request.getName());
            }
            producto.setName(request.getName());
        }

        if (request.getCategoria() != null) producto.setCategoria(request.getCategoria());
        if (request.getEtiqueta() != null) producto.setEtiqueta(request.getEtiqueta());
        if (request.getUnit() != null) producto.setUnit(request.getUnit());
        if (request.getReorderPoint() != null) producto.setReorderPoint(request.getReorderPoint());

        return productoRepositoryPort.updateProducto(producto);
    }
}
