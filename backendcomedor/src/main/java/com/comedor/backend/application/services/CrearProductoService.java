package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductoMapper;
import com.comedor.backend.application.ports.in.CrearProductoUseCase;
import com.comedor.backend.application.ports.out.CategoriaRepositoryPort;
import com.comedor.backend.application.ports.out.EtiquetaRepositoryPort;
import com.comedor.backend.application.ports.out.ProductoRepositoryPort;
import com.comedor.backend.domain.exceptions.ProductoExistenteException;
import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.domain.model.Etiqueta;
import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;

public class CrearProductoService implements CrearProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final ProductoMapper productoMapper;
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final EtiquetaRepositoryPort etiquetaRepositoryPort;

    public CrearProductoService(ProductoRepositoryPort productoRepositoryPort, ProductoMapper productoMapper, CategoriaRepositoryPort categoriaRepositoryPort, EtiquetaRepositoryPort etiquetaRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.productoMapper = productoMapper;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.etiquetaRepositoryPort = etiquetaRepositoryPort;
    }

    @Override
    public ProductoResponseDTO crearProducto(ProductoRequestDTO productoRequestDTO) {
        if(productoRepositoryPort.existByName(productoRequestDTO.getName().toUpperCase()))
        {
            throw new ProductoExistenteException("Ya existe un producto con ese nombre :"+productoRequestDTO.getName());
        }
        if (productoRequestDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        Categoria categoria = categoriaRepositoryPort.getCategoriaById(productoRequestDTO.getCategoryId());

        Etiqueta etiqueta = null;
        if (productoRequestDTO.getEtiquetaId() != null) {
            etiqueta = etiquetaRepositoryPort.getEtiquetaById(productoRequestDTO.getEtiquetaId());
        }

        Producto producto = productoMapper.toDomain(productoRequestDTO);
        producto.setCategoria(categoria);
        producto.setEtiqueta(etiqueta);
        return  productoMapper.productoResponseDTO(productoRepositoryPort.createProducto(producto));
    }
}
