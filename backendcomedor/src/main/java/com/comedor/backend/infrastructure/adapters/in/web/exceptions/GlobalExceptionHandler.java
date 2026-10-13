package com.comedor.backend.infrastructure.adapters.in.web.exceptions;

import com.comedor.backend.domain.exceptions.*;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.InsufficientStockResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MissingProductResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(404)
                .body(new CustomErrorResponse(PeruTime.now(), ex.getMessage(), ""));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<CustomErrorResponse> handleBadCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(401)
                .body(new CustomErrorResponse(PeruTime.now(), ex.getMessage(), ""));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DataIntegrityViolationException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("error", "DATO_DUPLICADO");
        response.put("message", "Ya existe un registro duplicado");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<InsufficientStockResponseDTO> handleStockInsuficiente(
            InsufficientStockException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new InsufficientStockResponseDTO(
                                ex.getFaltantes().stream().map(f -> new MissingProductResponseDTO(
                                                f.getProductId(),
                                                f.getProductName(),
                                                f.getProductUnit(),
                                                f.getQuantityNeeded()
                                        ))
                                        .toList()
                        )
                );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CustomErrorResponse(PeruTime.now(), ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Falla no controlada en [{}]: ", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(
                        PeruTime.now(),
                        "Ocurrió un error interno en el servidor. Por favor, intente más tarde.",
                        request.getRequestURI()
                ));
    }


}