package com.ccnta.app.exception;

public class TokenNotFoundException extends RuntimeException {

    public TokenNotFoundException(String message){
        super(message);
    }
}
