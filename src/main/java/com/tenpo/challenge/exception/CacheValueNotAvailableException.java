package com.tenpo.challenge.exception;

public class CacheValueNotAvailableException extends RuntimeException {
    
    public CacheValueNotAvailableException(String message) {
        super(message);
    }
}