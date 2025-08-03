package com.banquito.core.bank.transaction.exception;

public class TransaccionException extends RuntimeException {

    public TransaccionException(String mensaje) {
        super(mensaje);
    }

    public TransaccionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
