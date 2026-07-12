package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.EditProductUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.exceptions.InvalidProductUnitException;
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

        Integer actualTagId = product.getTag() != null
                ? product.getTag().getId()
                : 0;

        String unidadNormalizada = null;

        if (request.getUnit() != null) {
            unidadNormalizada = normalizarUnidad(request.getUnit());
        }

        boolean nameChanged =
                request.getName() != null &&
                        !request.getName().equalsIgnoreCase(product.getName());

        boolean categoryChanged =
                request.getCategoryId() != null &&
                        !Objects.equals(request.getCategoryId(), product.getCategory().getId());

        boolean tagChanged =
                request.getTagId() != null &&
                        !Objects.equals(request.getTagId(), actualTagId);

        boolean unitChanged =
                unidadNormalizada != null &&
                        !unidadNormalizada.equalsIgnoreCase(product.getUnit());

        if (tieneTransacciones) {
            boolean intentaCambiarCamposBloqueados =
                    nameChanged ||
                            categoryChanged ||
                            tagChanged ||
                            unitChanged;

            if (intentaCambiarCamposBloqueados) {
                throw new ProductWithTransactionsException(
                        "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden"
                );
            }

            if (request.getReorderPoint() != null &&
                    !request.getReorderPoint().equals(product.getReorderPoint())) {

                registerModificationUseCase.registrar(new ModificationsRequestDTO(
                        "Producto",
                        product.getName(),
                        "reorderPoint",
                        product.getReorderPoint().toString(),
                        request.getReorderPoint().toString()
                ));

                product.setReorderPoint(request.getReorderPoint());
            }

            return productMapper.productoResponseDTO(
                    productRepositoryPort.updateProducto(product)
            );
        }

        if (nameChanged) {
            String nuevoNombre = request.getName().toUpperCase();

            if (productRepositoryPort.existByNameAndIdNot(nuevoNombre, id)) {
                throw new ProductAlreadyExistsException(
                        "Ya existe un producto con el nombre: " + request.getName()
                );
            }

            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto",
                    product.getName(),
                    "name",
                    product.getName(),
                    request.getName()
            ));

            product.setName(request.getName());
        }

        if (categoryChanged) {
            Category newcategory =
                    categoryRepositoryPort.getCategoryById(request.getCategoryId());

            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto",
                    product.getName(),
                    "category",
                    product.getCategory().getName(),
                    newcategory.getName()
            ));

            product.setCategory(newcategory);
        }

        if (tagChanged) {
            Integer requestedTagId = request.getTagId();

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

        if (unitChanged) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto",
                    product.getName(),
                    "unit",
                    product.getUnit(),
                    unidadNormalizada
            ));

            product.setUnit(unidadNormalizada);
        }

        if (request.getReorderPoint() != null &&
                !request.getReorderPoint().equals(product.getReorderPoint())) {

            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Producto",
                    product.getName(),
                    "reorderPoint",
                    product.getReorderPoint().toString(),
                    request.getReorderPoint().toString()
            ));

            product.setReorderPoint(request.getReorderPoint());
        }

        return productMapper.productoResponseDTO(
                productRepositoryPort.updateProducto(product)
        );
    }

    private String normalizarUnidad(String unit) {
        String unidad = unit.trim().toUpperCase();

        return switch (unidad) {
            case "KILOGRAMO", "KILOGRAMOS", "KILO", "KILOS", "KG" -> "KG";
            case "LITRO", "LITROS", "L" -> "L";
            case "UNIDAD", "UNIDADES" -> "UNIDADES";
            case "SACO", "SACOS" -> "SACOS";
            case "LATA", "LATAS" -> "LATAS";
            case "BOLSA", "BOLSAS" -> "BOLSAS";
            default -> throw new InvalidProductUnitException("Unidad de medida no permitida");
        };
    }
}
