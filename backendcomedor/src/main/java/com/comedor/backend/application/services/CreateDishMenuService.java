package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DishMenuMapper;
import com.comedor.backend.application.ports.in.CreateDishMenuUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDishMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateDishMenuService implements CreateDishMenuUseCase {

    private final DishMenuRepositoryPort dishMenuRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final DishMenuMapper dishMenuMapper;
    private final RegisterAuditUseCase registerAuditUseCase;

    public CreateDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepositoryPort, DishMenuMapper dishMenuMapper, RegisterAuditUseCase registerAuditUseCase) {
        this.dishMenuRepositoryPort = dishMenuRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.dishMenuMapper = dishMenuMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }


    @Override
    public DishMenuResponseDTO create(CreateDishMenuRequestDTO request) {
        if (dishMenuRepositoryPort.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un plato con ese nombre: " + request.getName());
        }

        DishMenu dishMenu = new DishMenu();
        dishMenu.setName(request.getName().toUpperCase());
        dishMenu.setStatus(Status.ACTIVO);

        List<DishSupply> supplies = request.getSupplies().stream().map(s -> {
            Product product = productRepositoryPort.getProductoById(s.getProductId());
            DishSupply supply = new DishSupply();
            supply.setProduct(product);
            supply.setQuantityNeeded(s.getQuantityNeeded());
            supply.setDishMenu(dishMenu);
            return supply;
        }).toList();

        dishMenu.setSupplies(supplies);

        DishMenu savedDishMenu =
                dishMenuRepositoryPort.save(dishMenu);
        // Luego se registra la creación en auditoría.
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Plato",
                        savedDishMenu.getId(),
                        savedDishMenu.getName(),
                        AuditAction.CREACION,
                        Map.of(
                                "Nombre",
                                savedDishMenu.getName(),
                                "Estado",
                                savedDishMenu.getStatus().toString(),
                                "Insumos",
                                savedDishMenu.getSupplies()
                                        .stream()
                                        .map(supply -> Map.of(
                                                "producto",
                                                supply.getProduct().getName(),
                                                "cantidad",
                                                supply.getQuantityNeeded()
                                        ))
                                        .collect(Collectors.toList())
                        )
                )
        );
        return dishMenuMapper.toDto(savedDishMenu);
    }
}
