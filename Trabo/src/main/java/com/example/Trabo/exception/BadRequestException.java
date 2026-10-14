package com.example.Trabo.exception;

/** Requisição inválida do cliente (HTTP 400). */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
