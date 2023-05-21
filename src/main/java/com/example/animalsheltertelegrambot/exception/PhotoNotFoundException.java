package com.example.animalsheltertelegrambot.exception;

public class PhotoNotFoundException extends RuntimeException{
    private final long id;
    public PhotoNotFoundException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
