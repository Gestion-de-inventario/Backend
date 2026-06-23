package com.comedor.backend.infrastructure.adapters.in.web;


import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.domain.exceptions.ProductWithTransactionsException;
import com.comedor.backend.domain.exceptions.ProductNotFoundException;
import com.comedor.backend.domain.exceptions.ProductAlreadyExistsException;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final ListProductsByStatusUseCase listProductsByStatusUseCase;
    private final ActivateProductUseCase activateProductUseCase;
    private final DeactivateProductUseCase deactivateProductUseCase;
    private final EditProductUseCase editProductUseCase;

    @PreAuthorize("hasAuthority('PRODUCT_LIST_BY_STATUS')")
    @GetMapping("/list")
    public List<ProductResponseDTO> listarProductos(@RequestParam(required = false) Status status)
    {
        return listProductsByStatusUseCase.listarProductosPorEstado(status);
    }

    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @PostMapping("/create")
    public ProductResponseDTO crearProducto(@RequestBody ProductRequestDTO productRequestDTO)
    {
        return createProductUseCase.crearProducto(productRequestDTO);
    }

    @PreAuthorize("hasAuthority('PRODUCT_CHANGE_STATUS')")
    @PostMapping("/changeStatus/{id}")
    public ProductResponseDTO cambiarEstado(@PathVariable int id, @RequestParam Status status)
    {

        if (status == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        return switch (status) {
            case ACTIVO -> activateProductUseCase.activarProductoPorId(id);
            case INACTIVO -> deactivateProductUseCase.desactivarProductoPorId(id);
        };
    }

    @PreAuthorize("hasAuthority('PRODUCT_EDIT')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editarProducto(@PathVariable int id, @RequestBody EditProductRequestDTO request) {
        try {
            return ResponseEntity.ok(editProductUseCase.editar(id, request));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ProductWithTransactionsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ProductAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar el producto");
        }
    }

}
