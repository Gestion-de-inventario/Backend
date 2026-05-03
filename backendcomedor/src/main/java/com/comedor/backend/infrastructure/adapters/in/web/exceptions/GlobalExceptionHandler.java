package com.comedor.backend.infrastructure.adapters.in.web.exceptions;

import com.comedor.backend.domain.exceptions.CredencialesInvalidasException;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

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
}