package com.comedor.backend.infrastructure.adapters.in.web;


import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.domain.exceptions.ProductoConTransaccionesException;
import com.comedor.backend.domain.exceptions.ProductoNoEncontradoException;
import com.comedor.backend.domain.exceptions.ProductoYaExisteException;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductoRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoResponseDTO;
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

    private final CrearProductoUseCase crearProductoUseCase;
    private final ListarProductosPorEstadoUseCase listarProductosPorEstadoUseCase;
    private final ActivarProductoUseCase activarProductoUseCase;
    private final DesactivarProductoUseCase desactivarProductoUseCase;
    private final EditarProductoUseCase editarProductoUseCase;

    @PreAuthorize("hasAuthority('PRODUCT_LIST_BY_STATUS')")
    @GetMapping("/list")
    public List<ProductoResponseDTO> listarProductos(@RequestParam(required = false) Estado estado)
    {
        return listarProductosPorEstadoUseCase.listarProductosPorEstado(estado);
    }

    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @PostMapping("/create")
    public ProductoResponseDTO crearProducto(@RequestBody ProductoRequestDTO productoRequestDTO)
    {
        return crearProductoUseCase.crearProducto(productoRequestDTO);
    }

    @PreAuthorize("hasAuthority('PRODUCT_CHANGE_STATUS')")
    @PostMapping("/changeStatus/{id}")
    public ProductoResponseDTO cambiarEstado(@PathVariable int id, @RequestParam Estado estado)
    {

        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        return switch (estado) {
            case ACTIVO -> activarProductoUseCase.activarProductoPorId(id);
            case INACTIVO -> desactivarProductoUseCase.desactivarProductoPorId(id);
        };
    }

    @PreAuthorize("hasAuthority('PRODUCT_EDIT')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editarProducto(@PathVariable int id, @RequestBody EditarProductoRequestDTO request) {
        try {
            return ResponseEntity.ok(editarProductoUseCase.editar(id, request));
        } catch (ProductoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ProductoConTransaccionesException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ProductoYaExisteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar el producto");
        }
    }

}
