package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.RegistroProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroProductoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RegistroProductoMapper {

    public Registro toDomain(RegistroProductoRequestDTO requestDTO)
    {
        Registro registro = new Registro();

        Producto producto = new Producto();
        producto.setId(requestDTO.getProductoId());
        registro.setProduct(producto);

        registro.setAmount(requestDTO.getAmount());
        registro.setFuenteProducto(requestDTO.getFuenteProducto());
        registro.setUnitPrice(requestDTO.getUnitPrice());
        return registro;
    }

    public RegistroProductoResponseDTO toDto(Registro registro)
    {
        RegistroProductoResponseDTO responseDTO = new RegistroProductoResponseDTO();
        if(registro.getProduct() != null)
        {
            responseDTO.setProductoId(registro.getProduct().getId());
            responseDTO.setProductName(registro.getProduct().getName());
            responseDTO.setProductCategory(registro.getProduct().getCategoria().getName());
            responseDTO.setProductUnit(registro.getProduct().getUnit());
        }
        responseDTO.setSourceProduct(registro.getFuenteProducto());
        responseDTO.setAmount(registro.getAmount());
        responseDTO.setSpentAmount(registro.getAmount().multiply(registro.getUnitPrice()));
        return responseDTO;
    }
}
