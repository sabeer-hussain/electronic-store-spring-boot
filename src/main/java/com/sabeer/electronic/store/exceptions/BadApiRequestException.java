package com.sabeer.electronic.store.exceptions;

public class BadApiRequestException extends RuntimeException {

    public BadApiRequestException() {
        super("Bad Request !!");
    }

    public BadApiRequestException(String message) {
        super(message);
    }
}
