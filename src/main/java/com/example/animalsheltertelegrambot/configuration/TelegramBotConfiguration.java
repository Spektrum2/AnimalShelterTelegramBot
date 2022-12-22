package com.example.animalsheltertelegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигуратор
 */
@Configuration
public class TelegramBotConfiguration {
    /**
     * Объявление токена бота
     */
    @Value("${telegram.bot.token}")
    private String token;

    /**
     * Создание бота с помощью токена
     *
     * @return возвращает бота
     */
    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }
}
