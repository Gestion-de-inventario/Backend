package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.EditProductUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductWithTransactionsException;
import com.comedor.backend.domain.exceptions.ProductAlreadyExistsException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

import java.util.Objects;

public class EditProductService implements EditProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final RegisterModificationUseCase registerModificationUseCase;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final TagRepositoryPort tagRepositoryPort;
    private final ProductMapper productMapper;

    public EditProductService(ProductRepositoryPort productRepositoryPort, RegisterModificationUseCase registerModificationUseCase, CategoryRepositoryPort categoryRepositoryPort, TagRepositoryPort tagRepositoryPort, ProductMapper productMapper) {
        this.productRepositoryPort = productRepositoryPort;
        this.registerModificationUseCase = registerModificationUseCase;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.tagRepositoryPort = tagRepositoryPort;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponseDTO editar(Integer id, EditProductRequestDTO request) {
        Product product = productRepositoryPort.getProductoById(id);
        boolean tieneTransacciones = productRepositoryPort.tieneTransaccionesVinculadas(id);

        System.out.println("Editanto, este es el payload" + request.toString()  +"\nProducto actual"+product.toString());
        int actualTagId =0;
        if(product.getTag()!=null)
        {
            actualTagId=product.getTag().getId();
        }


        if (tieneTransacciones) {
            boolean intentaCambiarCamposBloqueados =
                    (request.getName() != null && !request.getName().equalsIgnoreCase(product.getName())) ||
                            (request.getCategoryId() != product.getCategory().getId()) ||
                            (request.getTagId() != actualTagId)  ||
                            (!request.getUnit().equals(product.getUnit()));

            System.out.println(intentaCambiarCamposBloqueados);
            if (intentaCambiarCamposBloqueados) {
                throw new ProductWithTransactionsException(
                        "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden"
                );
            }

            if (request.getReorderPoint() != null && !request.getReorderPoint().equals(product.getReorderPoint())) {
                registerModificationUseCase.registrar(new ModificationsRequestDTO(
                        "Producto",
                        product.getName(),
                        "reorderPoint",
                        product.getReorderPoint().toString(),
                        request.getReorderPoint().toString()
                ));
                product.setReorderPoint(request.getReorderPoint());
            }

            return productMapper.productoResponseDTO(productRepositoryPort.updateProducto(product));
        }

        if (request.getName() != null && !request.getName().equalsIgnoreCase(product.getName())) {
            if (productRepositoryPort.existByNameAndIdNot(request.getName(), id)) {
                throw new ProductAlreadyExistsException("Ya existe un producto con el nombre: " + request.getName());
            }
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto",product.getName(), "name", product.getName(), request.getName()
            ));
            product.setName(request.getName());
        }
        Category newcategory = categoryRepositoryPort.getCategoryById(request.getCategoryId());


        if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategory().getId())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto",product.getName(), "category",
                    product.getCategory().getName(),
                    newcategory.getName()
            ));
            product.setCategory(newcategory);
        }

        Integer requestedTagId = request.getTagId();
        Integer currentTagId = product.getTag() != null
                ? product.getTag().getId()
                : 0;
        System.out.println("currentTagId: "+currentTagId + "requestedTagId: "+requestedTagId);
        if (requestedTagId != null && !Objects.equals(requestedTagId, currentTagId)) {

            String oldTagName = product.getTag() != null
                    ? product.getTag().getName()
                    : "Sin etiqueta";

            if (requestedTagId == 0) {

                registerModificationUseCase.registrar(new ModificationsRequestDTO(
                        "Producto",
                        product.getName(),
                        "tag",
                        oldTagName,
                        "Sin etiqueta"
                ));

                product.setTag(null);

            } else {

                Tag newtag = tagRepositoryPort.getTagById(requestedTagId);

                registerModificationUseCase.registrar(new ModificationsRequestDTO(
                        "Producto",
                        product.getName(),
                        "tag",
                        oldTagName,
                        newtag.getName()
                ));

                product.setTag(newtag);
            }
        }

        if (request.getUnit() != null && !request.getUnit().equalsIgnoreCase(product.getUnit())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto",
                    product.getName(),
                    "unit", product.getUnit(), request.getUnit()
            ));
            product.setUnit(request.getUnit());
        }

        if (request.getReorderPoint() != null && !request.getReorderPoint().equals(product.getReorderPoint())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto",
                    product.getName(),
                    "reorderPoint",
                    product.getReorderPoint().toString(),
                    request.getReorderPoint().toString()
            ));
            product.setReorderPoint(request.getReorderPoint());
        }

        return productMapper.productoResponseDTO((productRepositoryPort.updateProducto(product)));
    }
}
