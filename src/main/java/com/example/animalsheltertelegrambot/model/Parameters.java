package com.example.animalsheltertelegrambot.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Parameters {
    @Id
    private long chatId;
    private int shelter;
    private int chat;
    private int report;
    private int add;
    private String text;
    @OneToOne
    private PhotoOfAnimal photoOfAnimal;

    public Parameters() {
    }

    public Parameters(long chatId, int shelter) {
        this.chatId = chatId;
        this.shelter = shelter;
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

    public int getChat() {
        return chat;
    }

    public void setChat(int chat) {
        this.chat = chat;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PhotoOfAnimal getPhotoOfAnimal() {
        return photoOfAnimal;
    }

    public void setPhotoOfAnimal(PhotoOfAnimal photoOfAnimal) {
        this.photoOfAnimal = photoOfAnimal;
    }

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }
}
