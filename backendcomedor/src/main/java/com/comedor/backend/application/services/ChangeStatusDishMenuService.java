package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DishMenuMapper;
import com.comedor.backend.application.ports.in.ChangeStatusDishMenuUseCase;

import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.domain.model.enums.Status;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

import java.util.Map;

public class ChangeStatusDishMenuService implements ChangeStatusDishMenuUseCase {
    private final DishMenuRepositoryPort dishMenuRepositoryPort;
    private final RegisterAuditUseCase registerAuditUseCase;
    private final DishMenuMapper dishMenuMapper;

    public ChangeStatusDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, RegisterAuditUseCase registerAuditUseCase, DishMenuMapper dishMenuMapper) {
        this.dishMenuRepositoryPort = dishMenuRepositoryPort;
        this.registerAuditUseCase = registerAuditUseCase;
        this.dishMenuMapper = dishMenuMapper;
    }


    @Override
    public DishMenuResponseDTO changeStatus(Integer id, Status status) {
        DishMenu dishMenu = dishMenuRepositoryPort.findById(id);

        if(dishMenu.getStatus() == status){
            return dishMenuMapper.toDto(dishMenu);
        }

        dishMenu.setStatus(status);
        DishMenu saved = dishMenuRepositoryPort.save(dishMenu);

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Plato",
                        id,
                        dishMenu.getName(),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue",  dishMenu.getStatus().toString(),
                                "newValue", status.toString()
                        )
                )
        );

        return dishMenuMapper.toDto(saved);
    }
}
