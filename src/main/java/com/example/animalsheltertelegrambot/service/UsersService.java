package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.listener.TelegramBotUpdatesListener;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    public TelegramBotUpdatesListener telegramBotUpdatesListener;

    public UsersService(TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }
}
