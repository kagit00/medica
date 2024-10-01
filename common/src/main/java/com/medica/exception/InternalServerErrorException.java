package com.medica.exception;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String m) {
        super(m);
    }
}
