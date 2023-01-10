package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.example.animalsheltertelegrambot.service.PhotoOfAnimalService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;

import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для обработки сообщений
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    /**
     * Объявление logger для логирования
     */
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    /**
     * Объявление бота
     */
    private final TelegramBot telegramBot;

    /**
     * Обьявление перменной informationAboutTheShelter с описанием информации о приюте.
     */
    private final String informationAboutTheShelter = "В приюте животных из Астаны находится более 1700 бездомных собак, брошенных, потерянных и оказавшихся на улице при разных обстоятельствах. " +
            "Дворняги, метисы и породистые. У каждой собаки своя история и свой характер. Многие из них в какой-то момент оказались не нужной игрушкой - их предал хозяин. " +
            "Наш бот создан для того чтобы собаки из приюта обрели свой  дом и получили второй шанс на жизнь. " +
            "Так же мы привлекаем новых волонтеров для помощи приютским собакам.";

    /**
     * Обьявлние переменной workingHours с описанием работы приюта адреса.
     */
    private final String workingHours = "Приют животных из Астаны открыт для посещения 6 дней в неделю с 11:00 до 17:00 ч." +
            " Санитарные дни 1-е и 15-е число месяца (на эти дни приют закрыт для посещения).";

    /**
     * Обьявлние переменной securityMeasures с рекомендацией о технике безопасности на территории приюта.
     */
    private final String securityMeasures = "— Обувь должна быть на подошве, исключающей непроизвольное скольжение;" +
            "— верхняя одежда должна соответствовать погоде, исключать промокание, а также должна быть облегающей и исключать возможность непроизвольных зацепов за ограждения, строения и иные конструкции." +
            "Запрещается носить в карманах одежды колющие, режущие и стеклянные предметы." +
            "Возможно использование дополнительных средств индивидуальной защиты. Средства индивидуальной защиты должны соответствовать размеру, применяться в исправном, чистом состоянии по назначению и храниться в специально отведенных и оборудованных местах с соблюдением санитарных правил." +
            "При общении с животными работники и посетители приюта обязаны соблюдать меры персональной и общественной безопасности." +
            "При входе в какое-либо помещение или вольер или выходе из него необходимо обязательно закрыть дверь.";

    /**
     * parsePhone - регулярное выражение для парсинга строки
     */
    private final String parsePhone = "([+][7]-\\d{3}-\\d{3}-\\d{4})(\\s)([\\W+]+)";


    /**
     * Обьявление репозитория
     */
    private final UserRepository userRepository;

    private final PhotoOfAnimalService photoOfAnimalService;

    /**
     * Инжектим бота + репозиторий
     *
     * @param telegramBot          бот
     * @param userRepository       репозиторий
     * @param photoOfAnimalService обработка фотографии животного
     */
    public TelegramBotUpdatesListener(TelegramBot telegramBot, UserRepository userRepository, PhotoOfAnimalService photoOfAnimalService) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
        this.photoOfAnimalService = photoOfAnimalService;
    }

    /**
     * Настройка бота на получение входящих обновлений
     */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Метод для обработки сообщений
     *
     * @param updates available updates обновления бота
     * @return возвращает идентификатор последнего обработанного обновления или подтверждает их все
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
//            Обработка сообщений пользователя
            long reportId;
            if (update.message() != null && "/start".equals(update.message().text())) {
                //mainMenu(update.message().chat().id());
                testMenu(update.message().chat().id());
            } else if (update.message() != null && "Узнать информацию о приюте".equals(update.message().text())) {
                test2Menu(update.message().chat().id());
            } else if (update.message() != null && "Назад".equals(update.message().text())) {
                testMenu(update.message().chat().id());
            } else if (update.message() != null && update.message().document() == null && update.message().photo() == null && update.message().text().matches(parsePhone)) {
                parsing(update.message().text(), update.message().chat().id());
            } else if (update.message().photo() != null) {
                mailing(update.callbackQuery().message().chat().id(), "Пришлите имя животного" + "Например: Имя животного - Бобик");
                PhotoSize[] photoSizes = update.message().photo();
                reportId = photoOfAnimalService.uploadPhoto(photoSizes[2]);
            } else if (update.message().document() != null) {
                mailing(update.callbackQuery().message().chat().id(), "Пришлите имя животного" + "Например: Имя животного - Бобик");
                Document document = update.message().document();
                reportId = photoOfAnimalService.uploadPhoto(document);
            } else if (update.message() != null) {
                mailing(update.message().chat().id(), "Моя твоя не понимать");
            }
//            Конфигурирование нажатия кнопок во всех меню
            if (update.callbackQuery() != null) {
                String data = update.callbackQuery().data();
                switch (data) {
                    case "1" -> infoMenu(update.callbackQuery().message().chat().id());
                    case "text1" -> mailing(update.callbackQuery().message().chat().id(), informationAboutTheShelter);
                    case "text2" -> mailing(update.callbackQuery().message().chat().id(), workingHours);
                    case "text3" -> mailing(update.callbackQuery().message().chat().id(), securityMeasures);
                    case "BD" ->
                            mailing(update.callbackQuery().message().chat().id(), "Пожалуйста, введите сообщение в формате номер телефона + имя. " +
                                    "Например: +7-909-945-4367 Андрей");
                    case "5" ->
                            mailing(update.callbackQuery().message().chat().id(), "Переадресовываю Ваш запрос волонтеру, пожалуйста, ожидайте");
                    case "3" -> mailing(update.callbackQuery().message().chat().id(), "Пришлите фотографию");
                    default -> mailing(update.callbackQuery().message().chat().id(), data);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * Метод парсит стороку и заносит информацию в БД
     *
     * @param text сообщение пользователя содержащее номер телефона + имя
     * @param id   id чата
     */
    public void parsing(String text, Long id) {
        logger.info("Парсинг");
        Pattern pattern = Pattern.compile(parsePhone);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String phone = matcher.group(1);
            String name = matcher.group(3);
            UserData userData = new UserData(id, name, phone);
            userRepository.save(userData);
            mailing(id, "Контактные данные сохранены!");
        }
    }

    /**
     * Данные методы отпраляет сообщение пользователю
     *
     * @param chatId          id чата
     * @param receivedMessage текст сообщения пользователю
     * @param inlineKeyboard  текст сообщения меню.
     */
    public void mailing(long chatId, String receivedMessage, InlineKeyboardMarkup inlineKeyboard) {
        logger.info("Отправка сообщения");
        SendMessage message = new SendMessage(chatId, receivedMessage).replyMarkup(inlineKeyboard);
        SendResponse response = telegramBot.execute(message);
    }

    public void mailing(long chatId, String receivedMessage) {
        logger.info("Отправка сообщения");
        SendMessage message = new SendMessage(chatId, receivedMessage);
        SendResponse response = telegramBot.execute(message);
    }

    /**
     * Метод создает Главного меню
     *
     * @param chatId id чата
     */
    private void mainMenu(long chatId) {
        logger.info("Запуск метода с основным меню");
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Узнать информацию о приюте").callbackData("1");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Как взять собаку из приюта").callbackData("2");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Прислать отчет о питомце").callbackData("3");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Позвать волонтера").callbackData("4");
        inlineKeyboard.addRow(button1);
        inlineKeyboard.addRow(button2);
        inlineKeyboard.addRow(button3);
        inlineKeyboard.addRow(button4);
        mailing(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.", inlineKeyboard);
    }

    /**
     * Метод создает меню - информация о приюте
     *
     * @param chatId id чата
     */
    private void infoMenu(long chatId) {
        logger.info("Запуск метода вспомогательного меню");
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Рассказать о приюте").callbackData("text1");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Расписание работы приюта и адрес, схема проезда").callbackData("text2");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Рекомендации о технике безопасности на территории приюта").callbackData("text3");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Принять и записать контактные данные для связи").callbackData("BD");
        InlineKeyboardButton button5 = new InlineKeyboardButton("Позвать волонтера").callbackData("5");
        inlineKeyboard.addRow(button1);
        inlineKeyboard.addRow(button2);
        inlineKeyboard.addRow(button3);
        inlineKeyboard.addRow(button4);
        inlineKeyboard.addRow(button5);
        mailing(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.", inlineKeyboard);
    }

    private void testMenu(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{"Узнать информацию о приюте", "Как взять собаку из приюта"},
                new String[]{"Прислать отчет о питомце", "Позвать волонтера"});
        telegramBot.execute(new SendMessage(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.").replyMarkup(replyKeyboardMarkup));
    }

    private void test2Menu(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{"Рассказать о приюте", "Расписание работы приюта и адрес, схема проезда"},
                new String[]{"Рекомендации о технике безопасности на территории приюта", "Принять и записать контактные данные для связи"},
                new String[]{"Позвать волонтера", "Назад"});
        telegramBot.execute(new SendMessage(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.").replyMarkup(replyKeyboardMarkup));
    }

}
