package com.banquito.core.bank.transaction.exception;

public class CuentaNoEncontradaException extends RuntimeException {

    public CuentaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public CuentaNoEncontradaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
