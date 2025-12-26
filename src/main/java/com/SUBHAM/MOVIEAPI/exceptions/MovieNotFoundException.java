package com.SUBHAM.MOVIEAPI.exceptions;


public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String message) {
        super(message);
    }
}
