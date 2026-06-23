package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DishMenuMapper;
import com.comedor.backend.application.ports.in.ChangeStatusDishMenuUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

public class ChangeStatusDishMenuService implements ChangeStatusDishMenuUseCase {
    private final DishMenuRepositoryPort dishMenuRepositoryPort;
    private final RegisterModificationUseCase registerModificationUseCase;
    private final DishMenuMapper dishMenuMapper;

    public ChangeStatusDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, RegisterModificationUseCase registerModificationUseCase, DishMenuMapper dishMenuMapper) {
        this.dishMenuRepositoryPort = dishMenuRepositoryPort;
        this.registerModificationUseCase = registerModificationUseCase;
        this.dishMenuMapper = dishMenuMapper;
    }


    @Override
    public DishMenuResponseDTO changeStatus(Integer id, Status status) {
        DishMenu dishMenu = dishMenuRepositoryPort.findById(id);

        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "DishMenu", "status",
                dishMenu.getStatus().toString(),
                status.toString()
        ));

        dishMenu.setStatus(status);
        return dishMenuMapper.toDto(dishMenuRepositoryPort.save(dishMenu));
    }
}
