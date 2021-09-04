package com.example.starter.Util;

public class NoStackRuntimeException extends RuntimeException {
    public NoStackRuntimeException(String message) {
        super(message, null,false, false);
    }

    public NoStackRuntimeException(Throwable cause) {
        super(cause.getMessage(),cause,false,false);
    }
}
