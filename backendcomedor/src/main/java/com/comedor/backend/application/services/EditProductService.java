package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditProductUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductWithTransactionsException;
import com.comedor.backend.domain.exceptions.ProductAlreadyExistsException;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class EditProductService implements EditProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final RegisterModificationUseCase registerModificationUseCase;

    public EditProductService(ProductRepositoryPort productRepositoryPort, RegisterModificationUseCase registerModificationUseCase) {
        this.productRepositoryPort = productRepositoryPort;
        this.registerModificationUseCase = registerModificationUseCase;
    }

    @Override
    public Product editar(int id, EditProductRequestDTO request) {
        Product product = productRepositoryPort.getProductoById(id);
        boolean tieneTransacciones = productRepositoryPort.tieneTransaccionesVinculadas(id);

        if (tieneTransacciones) {
            boolean intentaCambiarCamposBloqueados =
                    (request.getName() != null && !request.getName().equalsIgnoreCase(product.getName())) ||
                            (request.getCategory() != null) ||
                            (request.getTag() != null) ||
                            (request.getUnit() != null);

            if (intentaCambiarCamposBloqueados) {
                throw new ProductWithTransactionsException(
                        "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden"
                );
            }

            if (request.getReorderPoint() != null && !request.getReorderPoint().equals(product.getReorderPoint())) {
                registerModificationUseCase.registrar(new ModificationsRequestDTO(
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
                throw new ProductAlreadyExistsException("Ya existe un producto con el nombre: " + request.getName());
            }
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "name", product.getName(), request.getName()
            ));
            product.setName(request.getName());
        }

        if (request.getCategory() != null && !request.getCategory().equals(product.getCategory())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "category",
                    product.getCategory().getName(),
                    request.getCategory().getName()
            ));
            product.setCategory(request.getCategory());
        }

        if (request.getTag() != null && !request.getTag().equals(product.getTag())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "tag",
                    product.getTag().getName(),
                    request.getTag().getName()
            ));
            product.setTag(request.getTag());
        }

        if (request.getUnit() != null && !request.getUnit().equalsIgnoreCase(product.getUnit())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "unit", product.getUnit(), request.getUnit()
            ));
            product.setUnit(request.getUnit());
        }

        if (request.getReorderPoint() != null && !request.getReorderPoint().equals(product.getReorderPoint())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto", "reorderPoint",
                    product.getReorderPoint().toString(),
                    request.getReorderPoint().toString()
            ));
            product.setReorderPoint(request.getReorderPoint());
        }

        return productRepositoryPort.updateProducto(product);
    }
}
