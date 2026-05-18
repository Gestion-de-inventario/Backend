package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditarProductoUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductoConTransaccionesException;
import com.comedor.backend.domain.exceptions.ProductoYaExisteException;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class EditarProductoService implements EditarProductoUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public EditarProductoService (ProductRepositoryPort productRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.productRepositoryPort = productRepositoryPort;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
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

            if (request.getReorderPoint() != null && !request.getReorderPoint().equals(product.getReorderPoint())) {
                registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                        "Producto", "reorderPoint",
                        product.getReorderPoint().toString(),
                        request.getReorderPoint().toString()
                ));
                product.setReorderPoint(request.getReorderPoint());
            }

            return productRepositoryPort.updateProducto(product);
        }

        if (request.getName() != null && !request.getName().equalsIgnoreCase(product.getName())) {
            if (productRepositoryPort.existByNameAndIdNot(request.getName(), id)) {
                throw new ProductoYaExisteException("Ya existe un producto con el nombre: " + request.getName());
            }
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "name", product.getName(), request.getName()
            ));
            product.setName(request.getName());
        }

        if (request.getCategory() != null && !request.getCategory().equals(product.getCategory())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "category",
                    product.getCategory().getName(),
                    request.getCategory().getName()
            ));
            product.setCategory(request.getCategory());
        }

        if (request.getTag() != null && !request.getTag().equals(product.getTag())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "tag",
                    product.getTag().getName(),
                    request.getTag().getName()
            ));
            product.setTag(request.getTag());
        }

        if (request.getUnit() != null && !request.getUnit().equalsIgnoreCase(product.getUnit())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "unit", product.getUnit(), request.getUnit()
            ));
            product.setUnit(request.getUnit());
        }

        if (request.getReorderPoint() != null && !request.getReorderPoint().equals(product.getReorderPoint())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "reorderPoint",
                    product.getReorderPoint().toString(),
                    request.getReorderPoint().toString()
            ));
            product.setReorderPoint(request.getReorderPoint());
        }

        return productRepositoryPort.updateProducto(product);
    }
}
