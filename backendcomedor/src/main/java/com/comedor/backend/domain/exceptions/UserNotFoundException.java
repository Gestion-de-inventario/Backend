package com.comedor.backend.domain.exceptions;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String username) {
        super("Usuario no encontrado: " + username);
    }
}