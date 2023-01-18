package com.example.animalsheltertelegrambot.record;

import java.time.LocalDateTime;

public class UserRecord {
    private Long id;
    private Long chatId;
    private String name;
    private String phoneNumber;
    private Integer shelter;
    private LocalDateTime date;
    private AnimalRecord animal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdChat() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = this.chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getShelter() {
        return shelter;
    }

    public void setShelter(Integer shelter) {
        this.shelter = shelter;
    }

    public AnimalRecord getAnimal() {
        return animal;
    }

    public void setAnimal(AnimalRecord animal) {
        this.animal = animal;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
