package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DishMenuMapper;
import com.comedor.backend.application.ports.in.ChangeStatusDishMenuUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

public class ChangeStatusDishMenuService implements ChangeStatusDishMenuUseCase {
    private final DishMenuRepositoryPort dishMenuRepositoryPort;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;
    private final DishMenuMapper dishMenuMapper;

    public ChangeStatusDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase, DishMenuMapper dishMenuMapper) {
        this.dishMenuRepositoryPort = dishMenuRepositoryPort;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
        this.dishMenuMapper = dishMenuMapper;
    }


    @Override
    public DishMenuResponseDTO changeStatus(Integer id, Estado status) {
        DishMenu dishMenu = dishMenuRepositoryPort.findById(id);

        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "DishMenu", "status",
                dishMenu.getStatus().toString(),
                status.toString()
        ));

        dishMenu.setStatus(status);
        return dishMenuMapper.toDto(dishMenuRepositoryPort.save(dishMenu));
    }
}
