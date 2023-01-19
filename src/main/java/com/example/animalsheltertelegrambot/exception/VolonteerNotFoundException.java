package com.example.animalsheltertelegrambot.exception;

public class VolonteerNotFoundException extends RuntimeException {
    private final long id;

    public VolonteerNotFoundException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
