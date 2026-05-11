package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.common.mapper.ProductoMapper;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductoNoEncontradoException;
import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoriaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.EtiquetaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ProductoEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.CategoriaEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.EtiquetaEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.ProductoEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.ProductoJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.TransaccionJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository productoJpaRepository;
    private final ProductoEntityMapper productoEntityMapper;
    private final TransaccionJpaRepository transaccionJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Producto createProducto(Producto producto) {
        ProductoEntity entity = productoEntityMapper.toEntity(producto);

        entity.setCategoria(
                entityManager.getReference(
                        CategoriaEntity.class,
                        producto.getCategoria().getId()
                )
        );

        if (producto.getEtiqueta() != null) {
            entity.setEtiqueta(
                    entityManager.getReference(
                            EtiquetaEntity.class,
                            producto.getEtiqueta().getId()
                    )
            );
        }

        return productoEntityMapper.toDomain(
                productoJpaRepository.save(entity)
        );
    }

    @Override
    public List<Producto> getProductosByStatus(Estado estado) {
        if(estado == null)
        {   return productoEntityMapper.toListDomain(productoJpaRepository.findAll());
        } else if (estado == Estado.ACTIVO)
        {
            return productoEntityMapper.toListDomain(productoJpaRepository.getAllProductosActivos());
        }
        else if (estado == Estado.INACTIVO)
        {
        return productoEntityMapper.toListDomain(productoJpaRepository.getAllProductosInactivos());
        }
        return null;
    }

    @Override
    public boolean existByName(String nombre) {
        return productoJpaRepository.existsByName(nombre);
    }

    @Override
    public Producto deactivateById(int id) {
        ProductoEntity productoEntity = productoJpaRepository.findById(id).orElseThrow(()-> new ProductoNoEncontradoException("Producto no encontrado"));
        productoEntity.setStatus(Estado.INACTIVO);
        return productoEntityMapper.toDomain(productoJpaRepository.save(productoEntity));
    }

    @Override
    public Producto activateById(int id) {
        ProductoEntity productoEntity = productoJpaRepository.findById(id).orElseThrow(()-> new ProductoNoEncontradoException("Producto no encontrado"));
        productoEntity.setStatus(Estado.ACTIVO);
        return productoEntityMapper.toDomain(productoJpaRepository.save(productoEntity));
    }

    @Override
    public Producto getProductoById(int id) {
        ProductoEntity productoEntity = productoJpaRepository.findById(id).orElseThrow(()-> new ProductoNoEncontradoException("Producto no encontrado"));
        return productoEntityMapper.toDomain(productoEntity);
    }

    @Override
    public Producto updateProducto(Producto producto) {
        ProductoEntity entity = productoJpaRepository.findById(producto.getId())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado"));

        entity.setName(producto.getName());
        entity.setUnit(producto.getUnit());
        entity.setReorderPoint(producto.getReorderPoint());

        if (producto.getCategoria() != null) {
            entity.setCategoria(
                    entityManager.getReference(CategoriaEntity.class, producto.getCategoria().getId())
            );
        }

        return productoEntityMapper.toDomain(productoJpaRepository.save(entity));
    }

    @Override
    public Producto updateStock(Producto producto) {

        ProductoEntity entity = productoJpaRepository.findById(producto.getId())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado"));

        entity.setStock(producto.getStock());

        ProductoEntity saved = productoJpaRepository.save(entity);

        return productoEntityMapper.toDomain(saved);
    }
    @Override
    public boolean tieneTransaccionesVinculadas(int id) {
        return transaccionJpaRepository.existsByProductId(id);
    }

    @Override
    public boolean existByNameAndIdNot(String nombre, int id) {
        return productoJpaRepository.existsByNameAndIdNot(nombre.toLowerCase(), id);
    }
}
