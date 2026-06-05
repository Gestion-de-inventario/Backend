package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DishMenuMapper;
import com.comedor.backend.application.ports.in.CreateDishMenuUseCase;
import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDishMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

import java.util.List;

public class CreateDishMenuService implements CreateDishMenuUseCase {

    private final DishMenuRepositoryPort dishMenuRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final DishMenuMapper dishMenuMapper;

    public CreateDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepositoryPort, DishMenuMapper dishMenuMapper) {
        this.dishMenuRepositoryPort = dishMenuRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.dishMenuMapper = dishMenuMapper;
    }


    @Override
    public DishMenuResponseDTO create(CreateDishMenuRequestDTO request) {
        if (dishMenuRepositoryPort.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un plato con ese nombre: " + request.getName());
        }

        DishMenu dishMenu = new DishMenu();
        dishMenu.setName(request.getName().toUpperCase());
        dishMenu.setStatus(Estado.ACTIVO);

        List<DishSupply> supplies = request.getSupplies().stream().map(s -> {
            Product product = productRepositoryPort.getProductoById(s.getProductId());
            DishSupply supply = new DishSupply();
            supply.setProduct(product);
            supply.setQuantityNeeded(s.getQuantityNeeded());
            supply.setDishMenu(dishMenu);
            return supply;
        }).toList();

        dishMenu.setSupplies(supplies);

        return dishMenuMapper.toDto(dishMenuRepositoryPort.save(dishMenu));
    }
}
