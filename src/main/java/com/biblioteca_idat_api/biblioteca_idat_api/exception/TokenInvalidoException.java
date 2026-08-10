package com.biblioteca_idat_api.biblioteca_idat_api.exception;

public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException(String mensaje) {
        super(mensaje);
    }
}