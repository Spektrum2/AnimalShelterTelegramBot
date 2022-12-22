package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.listener.TelegramBotUpdatesListener;
import org.springframework.stereotype.Service;

@Service
public class VolunteerService {
    public TelegramBotUpdatesListener telegramBotUpdatesListener;

    public VolunteerService(TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }
}
