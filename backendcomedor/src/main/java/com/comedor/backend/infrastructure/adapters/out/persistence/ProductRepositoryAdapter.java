package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductNotFoundException;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoryEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TagEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.ProductEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.ProductJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.TransactionJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository productJpaRepository;
    private final ProductEntityMapper productEntityMapper;
    private final TransactionJpaRepository transactionJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Product createProducto(Product product) {
        ProductEntity entity = productEntityMapper.toEntity(product);

        entity.setCategory(
                entityManager.getReference(
                        CategoryEntity.class,
                        product.getCategory().getId()
                )
        );

        if (product.getTag() != null) {
            entity.setTag(
                    entityManager.getReference(
                            TagEntity.class,
                            product.getTag().getId()
                    )
            );
        }

        return productEntityMapper.toDomain(
                productJpaRepository.save(entity)
        );
    }

    @Override
    public List<Product> getProductosByStatus(Status status) {
        if(status == null)
        {   return productEntityMapper.toListDomain(productJpaRepository.findAll());
        } else if (status == Status.ACTIVO)
        {
            return productEntityMapper.toListDomain(productJpaRepository.getAllProductosActivos());
        }
        else if (status == Status.INACTIVO)
        {
        return productEntityMapper.toListDomain(productJpaRepository.getAllProductosInactivos());
        }
        return null;
    }

    @Override
    public boolean existByName(String nombre) {
        return productJpaRepository.existsByName(nombre);
    }

    @Override
    public Product deactivateById(int id) {
        ProductEntity productEntity = productJpaRepository.findById(id).orElseThrow(()-> new ProductNotFoundException("Producto no encontrado"));
        productEntity.setStatus(Status.INACTIVO);
        return productEntityMapper.toDomain(productJpaRepository.save(productEntity));
    }

    @Override
    public Product activateById(int id) {
        ProductEntity productEntity = productJpaRepository.findById(id).orElseThrow(()-> new ProductNotFoundException("Producto no encontrado"));
        productEntity.setStatus(Status.ACTIVO);
        return productEntityMapper.toDomain(productJpaRepository.save(productEntity));
    }

    @Override
    public Product getProductoById(int id) {
        ProductEntity productEntity = productJpaRepository.findById(id).orElseThrow(()-> new ProductNotFoundException("Producto no encontrado"));
        return productEntityMapper.toDomain(productEntity);
    }

    @Override
    public Product updateProducto(Product product) {
        ProductEntity entity = productJpaRepository.findById(product.getId())
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        entity.setName(product.getName());
        entity.setUnit(product.getUnit());
        entity.setReorderPoint(product.getReorderPoint());

        if (product.getCategory() != null) {
            entity.setCategory(
                    entityManager.getReference(CategoryEntity.class, product.getCategory().getId())
            );
        }
        if (product.getTag() != null) {
            entity.setTag(
                    entityManager.getReference(
                            TagEntity.class,
                            product.getTag().getId()
                    )
            );
        }else {
            entity.setTag(null);
        }

        return productEntityMapper.toDomain(productJpaRepository.save(entity));
    }

    @Override
    public Product updateStock(Product product) {

        ProductEntity entity = productJpaRepository.findById(product.getId())
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        entity.setStock(product.getStock());

        ProductEntity saved = productJpaRepository.save(entity);

        return productEntityMapper.toDomain(saved);
    }
    @Override
    public boolean tieneTransaccionesVinculadas(Integer id) {
        return transactionJpaRepository.existsByReferenceId(id);
    }

    @Override
    public boolean existByNameAndIdNot(String nombre, int id) {
        return productJpaRepository.existsByNameAndIdNot(nombre.toUpperCase(), id);
    }

    @Override
    public List<Product> getProductosBajoStockMinimo() {
        return productEntityMapper.toListDomain(
                productJpaRepository.getProductosBajoStockMinimo()
        );
    }
}
