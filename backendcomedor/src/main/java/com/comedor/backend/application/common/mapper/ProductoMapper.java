package com.comedor.backend.application.common.mapper;


import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.domain.model.Etiqueta;
import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductoMapper {
    public Producto toDomain(ProductoRequestDTO dto) {

        Producto producto = new Producto();

        producto.setName(dto.getName());

        if (dto.getCategoryId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getCategoryId());
            producto.setCategoria(categoria);
        }

        if (dto.getEtiquetaId() != null) {
            Etiqueta etiqueta = new Etiqueta();
            etiqueta.setId(dto.getEtiquetaId());
            producto.setEtiqueta(etiqueta);
        }

        producto.setUnit(dto.getUnit());
        producto.setStock(dto.getStock());
        producto.setReorderPoint(dto.getReorderPoint());

        return producto;
    }

    public ProductoResponseDTO productoResponseDTO(Producto producto)
    {
        if (producto == null)
            return null;
        ProductoResponseDTO productoResponseDTO = new ProductoResponseDTO();
        productoResponseDTO.setId(producto.getId());
        productoResponseDTO.setName(producto.getName());
        productoResponseDTO.setStatus(producto.getStatus());
        productoResponseDTO.setCategoryId(producto.getCategoria().getId());
        productoResponseDTO.setCategoryName(producto.getCategoria().getName());
        productoResponseDTO.setCategoryState(producto.getCategoria().getStatus());
        if (producto.getEtiqueta()!=null){
            productoResponseDTO.setEtiquetaId(producto.getEtiqueta().getId());
            productoResponseDTO.setEtiquetaName(producto.getEtiqueta().getName());
            productoResponseDTO.setEtiquetaState(producto.getEtiqueta().getStatus());
        }
        productoResponseDTO.setUnit(producto.getUnit());
        productoResponseDTO.setStock(producto.getStock());
        productoResponseDTO.setReorderPoint(producto.getReorderPoint());
        return productoResponseDTO;
    }

    public List<ProductoResponseDTO> toListProductoResponseDTO (List<Producto> productos)
    {
        if (productos == null)
            return null;
        return productos.stream().map(this::productoResponseDTO).toList();
    }

}
