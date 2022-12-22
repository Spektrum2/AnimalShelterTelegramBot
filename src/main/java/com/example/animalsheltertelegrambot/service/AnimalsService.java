package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.listener.TelegramBotUpdatesListener;
import org.springframework.stereotype.Service;

@Service
public class AnimalsService {
    public TelegramBotUpdatesListener telegramBotUpdatesListener;

    public AnimalsService(TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }
}
