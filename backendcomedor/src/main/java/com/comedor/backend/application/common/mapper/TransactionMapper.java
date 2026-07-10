package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransactionResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public Transactions toDomain(TransactionRequestDTO dto) {
        Transactions transaccion = new Transactions();
        transaccion.setReferenceType(dto.getReferenceType());
        if(dto.getReferenceId() != null)
        {
            transaccion.setReferenceId(dto.getReferenceId());
        }
        transaccion.setItemName(dto.getItemName().toUpperCase());
        transaccion.setType(dto.getType());
        transaccion.setAmount(dto.getAmount());
        transaccion.setSource(dto.getSource());
        transaccion.setDateTime(dto.getDateTime());

        User user = new User();
        user.setId(dto.getUserId());

        transaccion.setUser(user);
        return transaccion;
    }

    public TransactionResponseDTO toDTO(Transactions transaccion) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaccion.getId());

        dto.setDateTime(transaccion.getDateTime());
        dto.setType(transaccion.getType());
        dto.setSource(transaccion.getSource());
        dto.setAmount(transaccion.getAmount());
        dto.setCurrentStock(transaccion.getCurrentStock());
        dto.setFinalStock(transaccion.getFinalStock());
        dto.setFinalStock(transaccion.getFinalStock());

        dto.setReferenceType(transaccion.getReferenceType());
        dto.setItemName(transaccion.getItemName());

        dto.setUserId(transaccion.getUser().getId());
        dto.setUserName(transaccion.getUser().getUsername());
        dto.setPersonaName(transaccion.getUser().getPersona().getName());
        dto.setPersonaLastName(transaccion.getUser().getPersona().getLastname());
        return dto;
    }
}
