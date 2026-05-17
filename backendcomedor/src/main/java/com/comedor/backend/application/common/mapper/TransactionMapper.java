package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransaccionResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public Transactions toDomain(TransaccionRequestDTO dto) {
        Transactions transaccion = new Transactions();
        transaccion.setType(dto.getType());
        transaccion.setAmount(dto.getAmount());

        Product product = new Product();
        product.setId(dto.getProductId());
        transaccion.setCurrentStock(product.getStock());
        transaccion.setProduct(product);

        User user = new User();
        user.setId(dto.getUserId());

        transaccion.setUser(user);
        return transaccion;
    }

    public TransaccionResponseDTO toDTO(Transactions transaccion) {
        TransaccionResponseDTO dto = new TransaccionResponseDTO();
        dto.setId(transaccion.getId());
        dto.setDateTime(transaccion.getDateTime());
        dto.setType(transaccion.getType());
        dto.setAmount(transaccion.getAmount());
        dto.setCurrentStock(transaccion.getCurrentStock());
        dto.setFinalStock(transaccion.getFinalStock());
        dto.setFinalStock(transaccion.getFinalStock());
        dto.setProductId(transaccion.getProduct().getId());
        dto.setProductName(transaccion.getProduct().getName());
        dto.setUserId(transaccion.getUser().getId());
        dto.setUserName(transaccion.getUser().getUsername());
        dto.setPersonaName(transaccion.getUser().getPersona().getName());
        dto.setPersonaLastName(transaccion.getUser().getPersona().getLastname());
        return dto;
    }

    public List<TransaccionResponseDTO> toListDto(List<Transactions> transacciones) {
        return transacciones.stream().map(this::toDTO).toList();
    }
}
