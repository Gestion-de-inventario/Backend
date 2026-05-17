package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditarProductoUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductoConTransaccionesException;
import com.comedor.backend.domain.exceptions.ProductoYaExisteException;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarProductoRequestDTO;

public class EditarProductoService implements EditarProductoUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public EditarProductoService (ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public Product editar(int id, EditarProductoRequestDTO request) {
        Product product = productRepositoryPort.getProductoById(id);
        boolean tieneTransacciones = productRepositoryPort.tieneTransaccionesVinculadas(id);

        if (tieneTransacciones) {
            boolean intentaCambiarCamposBloqueados =
                    (request.getName() != null && !request.getName().equalsIgnoreCase(product.getName())) ||
                            (request.getCategory() != null) ||
                            (request.getTag() != null) ||
                            (request.getUnit() != null);

            if (intentaCambiarCamposBloqueados) {
                throw new ProductoConTransaccionesException(
                        "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden"
                );
            }

            if (request.getReorderPoint() != null) product.setReorderPoint(request.getReorderPoint());
            return productRepositoryPort.updateProducto(product);
        }

        if (request.getName() != null && !request.getName().equalsIgnoreCase(product.getName())) {
            if (productRepositoryPort.existByNameAndIdNot(request.getName(), id)) {
                throw new ProductoYaExisteException("Ya existe un producto con el nombre: " + request.getName());
            }
            product.setName(request.getName());
        }

        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getTag() != null) product.setTag(request.getTag());
        if (request.getUnit() != null) product.setUnit(request.getUnit());
        if (request.getReorderPoint() != null) product.setReorderPoint(request.getReorderPoint());

        return productRepositoryPort.updateProducto(product);
    }
}
