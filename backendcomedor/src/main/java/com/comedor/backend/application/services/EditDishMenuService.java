package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DishMenuMapper;
import com.comedor.backend.application.ports.in.EditDishMenuUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EditDishMenuRequestDTO;

import java.util.List;

public class EditDishMenuService implements EditDishMenuUseCase {
    private final DishMenuRepositoryPort dishMenuRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;
    private final DishMenuMapper dishMenuMapper;

    public EditDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase, DishMenuMapper dishMenuMapper) {
        this.dishMenuRepositoryPort = dishMenuRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
        this.dishMenuMapper = dishMenuMapper;
    }

    @Override
    public DishMenuResponseDTO edit(Integer id, EditDishMenuRequestDTO request) {
        DishMenu dishMenu = dishMenuRepositoryPort.findById(id);

        if (request.getName() != null && !request.getName().equalsIgnoreCase(dishMenu.getName())) {
            if (dishMenuRepositoryPort.existsByNameAndIdNot(request.getName(), id)) {
                throw new RuntimeException("Ya existe un plato con ese nombre: " + request.getName());
            }
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "DishMenu", "name", dishMenu.getName(), request.getName().toUpperCase()
            ));
            dishMenu.setName(request.getName().toUpperCase());
        }

        if (request.getSupplies() != null) {
            List<DishSupply> newSupplies = request.getSupplies().stream().map(s -> {
                Product product = productRepositoryPort.getProductoById(s.getProductId());
                DishSupply supply = new DishSupply();
                supply.setProduct(product);
                supply.setQuantityNeeded(s.getQuantityNeeded());
                supply.setDishMenu(dishMenu);
                return supply;
            }).toList();
            dishMenu.setSupplies(newSupplies);
        }

        return dishMenuMapper.toDto(dishMenuRepositoryPort.save(dishMenu));

    }
}
