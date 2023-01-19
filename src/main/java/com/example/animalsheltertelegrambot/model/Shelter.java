package com.example.animalsheltertelegrambot.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Shelter {
    @Id
    private long chatId;
    private int shelter;

    public Shelter(long chatId, int shelter) {
        this.chatId = chatId;
        this.shelter = shelter;
    }

    public Shelter() {
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public int getShelter() {
        return shelter;
    }

    public void setShelter(int shelter) {
        this.shelter = shelter;
    }

}
