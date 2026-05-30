package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ListDishMenusUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish-menus")
@RequiredArgsConstructor
public class DishMenuController {

    private final ListDishMenusUseCase listDishMenusUseCase;
    @PreAuthorize("hasAuthority('DISH_MENU_LIST_ALL')")
    @GetMapping
    public List<DishMenuResponseDTO> list() {

        return listDishMenusUseCase.list();
    }
}