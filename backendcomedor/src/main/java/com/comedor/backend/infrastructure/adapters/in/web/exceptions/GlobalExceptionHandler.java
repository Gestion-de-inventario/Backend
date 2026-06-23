package com.comedor.backend.infrastructure.adapters.in.web.exceptions;

import com.comedor.backend.domain.exceptions.InvalidCredentialsException;
import com.comedor.backend.domain.exceptions.InsufficientStockException;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.InsufficientStockResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
                                ex.getFaltantes()
                        )
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomErrorResponse> handleBusinessExceptions(RuntimeException ex,
                                                                        HttpServletRequest request) {

        CustomErrorResponse response = new CustomErrorResponse(
                PeruTime.now(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}