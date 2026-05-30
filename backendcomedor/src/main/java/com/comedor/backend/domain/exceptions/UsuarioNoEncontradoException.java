package com.comedor.backend.domain.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String username) {
        super("Usuario no encontrado: " + username);
    }
}