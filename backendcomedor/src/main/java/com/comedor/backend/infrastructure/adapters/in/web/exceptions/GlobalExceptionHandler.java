package com.comedor.backend.infrastructure.adapters.in.web.exceptions;

import com.comedor.backend.domain.exceptions.CredencialesInvalidasException;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<CustomErrorResponse> handleUserNotFound(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(404)
                .body(new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), ""));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<CustomErrorResponse> handleBadCredentials(CredencialesInvalidasException ex) {
        return ResponseEntity.status(401)
                .body(new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), ""));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DataIntegrityViolationException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("error", "DATO_DUPLICADO");
        response.put("message", "El usuario ya existe");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomErrorResponse> handleBusinessExceptions(RuntimeException ex,
                                                                        HttpServletRequest request) {

        CustomErrorResponse response = new CustomErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}