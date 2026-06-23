package com.comedor.backend.domain.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super("Usuario no encontrado: " + username);
    }
}