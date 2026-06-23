package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ChangeStatusDishMenuUseCase;
import com.comedor.backend.application.ports.in.CreateDishMenuUseCase;
import com.comedor.backend.application.ports.in.EditDishMenuUseCase;
import com.comedor.backend.application.ports.in.ListDishMenusUseCase;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDishMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EditDishMenuRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish-menus")
@RequiredArgsConstructor
public class DishMenuController {

    private final ListDishMenusUseCase listDishMenusUseCase;
    private final CreateDishMenuUseCase createDishMenuUseCase;
    private final EditDishMenuUseCase editDishMenuUseCase;
    private final ChangeStatusDishMenuUseCase changeStatusDishMenuUseCase;

    @PreAuthorize("hasAuthority('DISH_MENU_LIST_ALL')")
    @GetMapping
    public List<DishMenuResponseDTO> list() {
        return listDishMenusUseCase.list();
    }

    @PreAuthorize("hasAuthority('DISH_MENU_CREATE')")
    @PostMapping("/create")
    public DishMenuResponseDTO create(@Valid @RequestBody CreateDishMenuRequestDTO request) {
        return createDishMenuUseCase.create(request);
    }

    @PreAuthorize("hasAuthority('DISH_MENU_EDIT')")
    @PutMapping("/{id}")
    public DishMenuResponseDTO edit(@PathVariable Integer id,
                                    @RequestBody EditDishMenuRequestDTO request) {
        return editDishMenuUseCase.edit(id, request);
    }

    @PreAuthorize("hasAuthority('DISH_MENU_CHANGE_STATUS')")
    @PostMapping("/{id}/changeStatus")
    public DishMenuResponseDTO changeStatus(@PathVariable Integer id,
                                            @RequestParam Status status) {
        return changeStatusDishMenuUseCase.changeStatus(id, status);
    }
}