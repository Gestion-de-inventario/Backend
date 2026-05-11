package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.domain.model.Transacciones;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransaccionMapper {

    public Transacciones toDomain(TransaccionRequestDTO dto) {
        Transacciones transaccion = new Transacciones();
        transaccion.setType(dto.getType());
        transaccion.setAmount(dto.getAmount());

        Producto producto = new Producto();
        producto.setId(dto.getProductId());
        transaccion.setProduct(producto);

        Usuario usuario = new Usuario();
        usuario.setId(dto.getUserId());

        transaccion.setUser(usuario);
        return transaccion;
    }

    public TransaccionResponseDTO toDTO(Transacciones transaccion) {
        TransaccionResponseDTO dto = new TransaccionResponseDTO();
        dto.setId(transaccion.getId());
        dto.setDateTime(transaccion.getDateTime());
        dto.setType(transaccion.getType());
        dto.setAmount(transaccion.getAmount());
        dto.setProductId(transaccion.getProduct().getId());
        dto.setProductName(transaccion.getProduct().getName());
        dto.setUserId(transaccion.getUser().getId());
        dto.setUserName(transaccion.getUser().getUsername());
        dto.setPersonaName(transaccion.getUser().getPersona().getName());
        dto.setPersonaLastName(transaccion.getUser().getPersona().getLastname());
        return dto;
    }

    public List<TransaccionResponseDTO> toListDto(List<Transacciones> transacciones) {
        return transacciones.stream().map(this::toDTO).toList();
    }
}
