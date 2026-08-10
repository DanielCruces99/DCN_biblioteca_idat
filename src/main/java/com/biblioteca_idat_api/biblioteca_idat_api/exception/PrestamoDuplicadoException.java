package com.biblioteca_idat_api.biblioteca_idat_api.exception;

public class PrestamoDuplicadoException extends RuntimeException {
    public PrestamoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}