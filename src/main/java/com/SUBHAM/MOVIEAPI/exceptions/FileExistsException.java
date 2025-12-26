package com.SUBHAM.MOVIEAPI.exceptions;

public class FileExistsException extends RuntimeException {

    public FileExistsException(String message) {
        super(message);
    }
}