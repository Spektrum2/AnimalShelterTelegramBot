package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.example.animalsheltertelegrambot.service.PhotoOfAnimalService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    private final String securityData = "Для получение пропуска на территорию приюта, пожалуйста, езжайте к центральному входу, по приезду наберите номер 5959";
    private Integer animalType = 0;
    private final String rulesForGettingToKnowAnAnimal = "Веди себя хорошо, не балуйся";
    private final String listOfDocuments = "Паспорт";
    private final String animalTransportation = "Клетка для перевозки";
    private final String animalAdaptation = "Мыть, кормить, любить, не бить";
    private final String adultAnimalAdaptation = "Мыть, кормить, любить, не бить, выводить гулять";
    private final String adaptationOfAnAnimalWithDisabilities = "Мыть, кормить, любить, не бить, выводить гулять";
    private final String tipsFromADogHandler = "Не бить палкой";
    private final String recommendationForDogHandlers = "Тетя Зина, Дядя Толя";
    private final String reasonForRefusal = "Плохой запах";


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
            String text = update.message().text();
            Long chatId = update.message().chat().id();
            if (update.message() != null && text.matches(parsePhone)) {
                parsing(text, chatId);
            } else if (update.message() != null) {
                switch (text) {
                    case "/start" -> mainMenu(chatId);
                    case "Приют для собак" -> {
                        animalType = 1;
                        shelterMenu(chatId);
                    }
                    case "Приют для кошек" -> {
                        animalType = 2;
                        shelterMenu(chatId);
                    }
                    case "Узнать информацию о приюте" -> infoMenu(chatId);
                    case "Назад" -> mainMenu(chatId);
                    case "Рассказать о приюте" -> mailing(chatId, informationAboutTheShelter);
                    case "Расписание работы приюта и адрес, схема проезда" -> mailing(chatId, workingHours);
                    case "Рекомендации о технике безопасности на территории приюта" -> mailing(chatId, securityMeasures);
                    case "Принять и записать контактные данные для связи" -> mailing(chatId, "Пожалуйста, введите сообщение в формате номер телефона + имя. " +
                            "Например: +7-909-945-4367 Андрей");
                    case "Контактные данные охраны для оформления пропуска на машину" -> mailing(chatId, securityData);
                    case "Позвать волонтера" -> mailing(chatId, "Переадресовываю Ваш запрос волонтеру, пожалуйста, ожидайте");
                    case "Как взять животное из приюта" -> infoAboutTheAnimalMenu(chatId, animalType);
                    case "Правила знакомства с животным" -> mailing(chatId, rulesForGettingToKnowAnAnimal);
                    case "Список документов, необходимых для того, чтобы взять животное из приюта" -> mailing(chatId, listOfDocuments);
                    case "Рекомендации по транспортировке животного" -> mailing(chatId, animalTransportation);
                    case "Рекомендации по обустройству дома щенка", "Рекомендации по обустройству дома котенка" ->
                            mailing(chatId, animalAdaptation);
                    case "Рекомендации по обустройству дома взрослой собаки", "Рекомендации по обустройству дома взрослого кота/кошки"
                        -> mailing(chatId, adultAnimalAdaptation);
                    case "Советы кинолога по первичному общению с собакой" -> mailing(chatId, tipsFromADogHandler);
                    case "Рекомендации по проверенным кинологам для дальнейшего обращения к собакой" -> mailing(chatId, recommendationForDogHandlers);
                    case "Список причин, почему могут отказать в просьбе забрать собаку из приюта", "Список причин, почему могут отказать в просьбе забрать кота/кошку из приюта"
                            -> mailing(chatId, reasonForRefusal);
                    case "Записать контактные данные для связи" -> mailing(chatId, "Пожалуйста, введите сообщение в формате номер телефона + имя. " +
                            "Например: +7-909-945-4367 Андрей");
                    case "Рекомендаций по обустройству дома собаки с ограниченными возможностями", "Рекомендаций по обустройству дома кота/кошки с ограниченными возможностями" ->
                        mailing(chatId, adaptationOfAnAnimalWithDisabilities);
                    default -> mailing(chatId, "Моя твоя не понимать");
                }
            }
//            if (update.message() != null && "/start".equals(text)) {
//                mainMenu(chatId);
//            } else if (update.message() != null && "Приют для собак".equals(text)) {
//                animalType = 1;
//                shelterMenu(chatId);
//            } else if (update.message() != null && "Приют для кошек".equals(text)) {
//                animalType = 2;
//                shelterMenu(chatId);
//            } else if (update.message() != null && "Узнать информацию о приюте".equals(text)) {
//                infoMenu(chatId);
//            } else if (update.message() != null && "Назад".equals(text)) {
//                mainMenu(chatId);
//            } else if (update.message() != null) {
//                mailing(chatId, "Моя твоя не понимать");
//            }
//            Конфигурирование нажатия кнопок во всех меню
//            if (update.callbackQuery() != null) {
//                String data = update.callbackQuery().data();
//                switch (data) {
//                    case "1" -> infoMenu(update.callbackQuery().message().chat().id());
//                    case "text1" -> mailing(update.callbackQuery().message().chat().id(), informationAboutTheShelter);
//                    case "text2" -> mailing(update.callbackQuery().message().chat().id(), workingHours);
//                    case "text3" -> mailing(update.callbackQuery().message().chat().id(), securityMeasures);
//                    case "BD" ->
//                            mailing(update.callbackQuery().message().chat().id(), "Пожалуйста, введите сообщение в формате номер телефона + имя. " +
//                                    "Например: +7-909-945-4367 Андрей");
//                    case "5" ->
//                            mailing(update.callbackQuery().message().chat().id(), "Переадресовываю Ваш запрос волонтеру, пожалуйста, ожидайте");
//                    case "3" -> mailing(update.callbackQuery().message().chat().id(), "Пришлите фотографию");
//                    default -> mailing(update.callbackQuery().message().chat().id(), data);
//                }
//            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

//    private void savePhone(Long chatId, Integer animalType) {
//
//    }

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
     * @param replyKeyboardMarkup  текст сообщения меню.
     */
    public void mailing(long chatId, String receivedMessage, ReplyKeyboardMarkup replyKeyboardMarkup) {
        logger.info("Отправка сообщения");
        SendMessage message = new SendMessage(chatId, receivedMessage).replyMarkup(inlineKeyboard);
        SendResponse response = telegramBot.execute(message);
    }

    public void mailing(long chatId, String receivedMessage) {
        logger.info("Отправка сообщения");
        SendMessage message = new SendMessage(chatId, receivedMessage);
        SendResponse response = telegramBot.execute(message);
    }

//    /**
//     * Метод создает Главного меню
//     *
//     * @param chatId id чата
//     */
//    private void mainMenu(long chatId) {
//        logger.info("Запуск метода с основным меню");
//        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
//        InlineKeyboardButton button1 = new InlineKeyboardButton("Узнать информацию о приюте").callbackData("1");
//        InlineKeyboardButton button2 = new InlineKeyboardButton("Как взять собаку из приюта").callbackData("2");
//        InlineKeyboardButton button3 = new InlineKeyboardButton("Прислать отчет о питомце").callbackData("3");
//        InlineKeyboardButton button4 = new InlineKeyboardButton("Позвать волонтера").callbackData("4");
//        inlineKeyboard.addRow(button1);
//        inlineKeyboard.addRow(button2);
//        inlineKeyboard.addRow(button3);
//        inlineKeyboard.addRow(button4);
//        mailing(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.", inlineKeyboard);
//    }

//    /**
//     * Метод создает меню - информация о приюте
//     *
//     * @param chatId id чата
//     */
//    private void infoMenu(long chatId) {
//        logger.info("Запуск метода вспомогательного меню");
//        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
//        InlineKeyboardButton button1 = new InlineKeyboardButton("Рассказать о приюте").callbackData("text1");
//        InlineKeyboardButton button2 = new InlineKeyboardButton("Расписание работы приюта и адрес, схема проезда").callbackData("text2");
//        InlineKeyboardButton button3 = new InlineKeyboardButton("Рекомендации о технике безопасности на территории приюта").callbackData("text3");
//        InlineKeyboardButton button4 = new InlineKeyboardButton("Принять и записать контактные данные для связи").callbackData("BD");
//        InlineKeyboardButton button5 = new InlineKeyboardButton("Позвать волонтера").callbackData("5");
//        inlineKeyboard.addRow(button1);
//        inlineKeyboard.addRow(button2);
//        inlineKeyboard.addRow(button3);
//        inlineKeyboard.addRow(button4);
//        inlineKeyboard.addRow(button5);
//        mailing(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.", inlineKeyboard);
//    }


    private void mainMenu(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                ("Приют для собак"), "Приют для кошек");
                mailing(chatId, "Добрый день. Наш бот помогает найти новый дом брошенным животным. Пожалуйста, выберете интересующий Вас приют из меню ниже:", replyKeyboardMarkup);
    }
    private void shelterMenu(long chatId) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                    new String[]{"Узнать информацию о приюте", "Как взять животное из приюта"},
                    new String[]{"Прислать отчет о питомце", "Позвать волонтера"});
            mailing(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.", replyKeyboardMarkup);
    }
    private void infoMenu(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{"Рассказать о приюте", "Расписание работы приюта и адрес, схема проезда"},
                new String[]{"Рекомендации о технике безопасности на территории приюта", "Принять и записать контактные данные для связи"},
                new String[]{"Контактные данные охраны для оформления пропуска на машину", "Позвать волонтера"},
                new String[]{"Назад"});
        mailing(chatId, "Пожалуйста, выберете интересующую Вас информацию из списка ниже.", replyKeyboardMarkup);
    }

    private void infoAboutTheAnimalMenu(long chatId, int animalType) {
        if (animalType == 1) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                    new String[]{"Правила знакомства с животным", "Список документов, необходимых для того, чтобы взять животное из приюта"},
                    new String[]{"Рекомендации по транспортировке животного", "Рекомендации по обустройству дома щенка"},
                    new String[]{"Рекомендации по обустройству дома взрослой собаки", "Рекомендаций по обустройству дома собаки с ограниченными возможностями"},
                    new String[]{"Советы кинолога по первичному общению с собакой", "Рекомендации по проверенным кинологам для дальнейшего обращения к собакой"},
                    new String[]{"Список причин, почему могут отказать в просьбе забрать собаку из приюта", "Записать контактные данные для связи"},
                    new String[]{"Позвать волонтера", "Назад"});
            mailing(chatId, "Пожалуйста, выберете интересующую Вас информацию из списка ниже.", replyKeyboardMarkup);
        } else if (animalType == 2) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                    new String[]{"Правила знакомства с животным", "Список документов, необходимых для того, чтобы взять животное из приюта"},
                    new String[]{"Рекомендации по транспортировке животного", "Рекомендации по обустройству дома котенка"},
                    new String[]{"Рекомендации по обустройству дома взрослого кота/кошки", "Рекомендаций по обустройству дома кота/кошки с ограниченными возможностями"},
                    new String[]{"Список причин, почему могут отказать в просьбе забрать кота/кошку из приюта", "Записать контактные данные для связи"},
                    new String[]{"Позвать волонтера", "Назад"});
            mailing(chatId, "Пожалуйста, выберете интересующую Вас информацию из списка ниже.", replyKeyboardMarkup);
        }
    }

}
