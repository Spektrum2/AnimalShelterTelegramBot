package com.example.animalsheltertelegrambot.record;

import javax.validation.constraints.NotBlank;

public class VolunteerRecord {
    private long id;
    private Long chatId;
    @NotBlank(message = "Имя волонтера должно быть заполнено!")
    private String name;
    @NotBlank(message = "Фамилия волонтера должно быть заполнено!")
    private String lastName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
