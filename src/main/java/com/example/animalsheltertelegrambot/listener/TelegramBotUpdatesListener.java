package com.example.animalsheltertelegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {

            logger.info("Processing update: {}", update);
            long chatId = update.message().chat().id();
            String receivedMessage = update.message().text();
            String username = update.message().chat().username();
            String firstName = update.message().chat().firstName();
            String lastName = update.message().chat().lastName();

            if (receivedMessage.equals("/start")) {
                mailing(chatId,
                        "Добрый день. Рады приветствовать Вас в нашем приюте.");
            } else {
                mailing(chatId, "Переадрисовываю Ваш вопрос менеджеру.");
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    // Данный метод отпраляет сообщение пользователю
    public void mailing(long chatId, String receivedMessage) {
        logger.info("Отправка сообщения");
        SendMessage message = new SendMessage(chatId, receivedMessage);
        SendResponse response = telegramBot.execute(message);
    }
}
