package com.example.animalsheltertelegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private  final TelegramBot telegramBot;

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
            //Вызов главного меню с помощью сообщения /start
            if (update.message() != null && "/start".equals(update.message().text())) {
                telegramBot.execute(mainMenu(update.message().chat().id()));
            }
            //Конфигурирование нажатия кнопок во всех меню
            if (update.callbackQuery() != null) {
                if (update.callbackQuery().data().equals("1")) {
                    telegramBot.execute(infoMenu(update.callbackQuery().message().chat().id()));
                } else {
                    telegramBot.execute(new SendMessage(update.callbackQuery().message().chat().id(), update.callbackQuery().data()));
                }
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    // Данный метод отпраляет сообщение пользователю
//    public void mailing(long chatId, String receivedMessage) {
//        logger.info("Отправка сообщения");
//        SendMessage message = new SendMessage(chatId, receivedMessage);
//
//        SendResponse response = telegramBot.execute(message);
//    }

    //Метод создает Главного меню
    private SendMessage mainMenu(long chatId) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Узнать информацию о приюте").callbackData("1");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Как взять собаку из приюта").callbackData("2");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Прислать отчет о питомце").callbackData("3");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Позвать волонтера").callbackData("4");
        inlineKeyboard.addRow(button1);
        inlineKeyboard.addRow(button2);
        inlineKeyboard.addRow(button3);
        inlineKeyboard.addRow(button4);
        return new SendMessage(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.").replyMarkup(inlineKeyboard);
    }

    //Метод создает меню - информация о приюте
    private SendMessage infoMenu(long chatId) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Рассказать о приюте").callbackData("text1");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Расписание работы приюта и адрес, схему проезда").callbackData("text2");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Рекомендации о технике безопасности на территории приюта").callbackData("text3");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Принять и записать контактные данные для связи").callbackData("BD");
        InlineKeyboardButton button5 = new InlineKeyboardButton("Позвать волонтера").callbackData("5");
        inlineKeyboard.addRow(button1);
        inlineKeyboard.addRow(button2);
        inlineKeyboard.addRow(button3);
        inlineKeyboard.addRow(button4);
        inlineKeyboard.addRow(button5);
        return new SendMessage(chatId, "Добро пожаловать в меню - Информация о приюте.").replyMarkup(inlineKeyboard);
    }

}
