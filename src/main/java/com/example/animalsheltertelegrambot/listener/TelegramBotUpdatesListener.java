package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.Constants;
import com.example.animalsheltertelegrambot.model.PhotoOfAnimal;
import com.example.animalsheltertelegrambot.model.Report;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.repository.PhotoRepository;
import com.example.animalsheltertelegrambot.repository.ReportRepository;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.example.animalsheltertelegrambot.service.PhotoOfAnimalService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.animalsheltertelegrambot.model.Constants.*;

/**
 * Класс для обработки сообщений
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    /**
     * parsePhone - регулярное выражение для парсинга строки
     */
    private final String parsePhone = "([+][7]-\\d{3}-\\d{3}-\\d{4})(\\s)([\\W+]+)";
    private final String parseText = "([\\W+]+)/([\\W+]+)/([\\W+]+)";
    /**
     * Хранение значения, для разделения приютов для собак и кошек
     */
    private final Map<Long, Integer> save = new HashMap<>();
    private final Map<Long, PhotoSize> savePhotoSize = new HashMap<>();
    private final Map<Long, Document> saveDocument = new HashMap<>();
    private final Map<Long, String> saveText = new HashMap<>();
    /**
     * Объявление logger для логирования
     */
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    /**
     * Объявление бота
     */
    private final TelegramBot telegramBot;
    /**
     * Обьявление репозитория
     */
    private final UserRepository userRepository;
    /**
     * Объявления сервиса для обработки фотографии животного
     */
    private final PhotoRepository photoRepository;
    private final ReportRepository reportRepository;
    private final PhotoOfAnimalService photoOfAnimalService;

    /**
     * Инжектим бота + репозиторий
     *
     * @param telegramBot          бот
     * @param userRepository       репозиторий
     * @param photoOfAnimalService обработка фотографии животного
     */
    public TelegramBotUpdatesListener(TelegramBot telegramBot, UserRepository userRepository, PhotoOfAnimalService photoOfAnimalService,
                                      ReportRepository reportRepository,
                                      PhotoRepository photoRepository) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.photoRepository = photoRepository;
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
            Document document = update.message().document();
            PhotoSize[] photo = update.message().photo();
            int counter = 0;
            Integer animalType = 0;
            if (update.message() != null && photo == null && document == null && text.matches(parsePhone)) {
                parsing(text, chatId);
            } else if (update.message() != null && savePhotoSize.get(chatId) != null && saveDocument.get(chatId) != null && text.matches(parseText)) {
                saveText.put(chatId, text);
                mailing(chatId, "Пожалуйста, загрузите фотографию");
            } else if (update.message() != null && (photo != null || document != null) && saveText.get(chatId) != null) {
                if (document != null) {
                    saveDocument.put(chatId, document);
                    parsingAndSavePhoto(saveText.get(chatId), savePhotoSize.get(chatId), saveDocument.get(chatId), chatId);
                } else if (photo[1] != null) {
                    savePhotoSize.put(chatId, photo[1]);
                    parsingAndSavePhoto(saveText.get(chatId), savePhotoSize.get(chatId), saveDocument.get(chatId), chatId);
                }
            } else if (update.message() != null && (photo != null || document != null) && saveText.get(chatId) == null) {
                if (document != null) {
                    saveDocument.put(chatId, document);
                    mailing(chatId, "Пожалуйста, введите текст отчета");
                } else if (photo[1] != null) {
                    savePhotoSize.put(chatId, photo[1]);
                    mailing(chatId, "Пожалуйста, введите текст отчета");
                }
            } else if (update.message() != null && (savePhotoSize.get(chatId) != null || saveDocument.get(chatId) != null) && text.matches(parseText)) {
                saveText.put(chatId, text);
                parsingAndSavePhoto(saveText.get(chatId), savePhotoSize.get(chatId), saveDocument.get(chatId), chatId);
            } else if (update.message() != null && (savePhotoSize.get(chatId) != null || saveDocument.get(chatId) != null) && saveText.get(chatId) != null) {
                parsingAndSavePhoto(saveText.get(chatId), savePhotoSize.get(chatId), saveDocument.get(chatId), chatId);
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null) {
                switch (text) {
                    case "/start", "Выход" -> mainMenu(chatId);
                    case "Приют для собак" -> {
                        animalType = 1;
                        save.put(chatId, animalType);
                        shelterMenu(chatId);
                    }
                    case "Приют для кошек" -> {
                        animalType = 2;
                        save.put(chatId, animalType);
                        shelterMenu(chatId);
                    }
                    case "Узнать информацию о приюте" -> infoMenu(chatId);
                    case "Рассказать о приюте" -> {
                        if (save.get(chatId) == 1) {
                            mailing(chatId, INFORMATION_ABOUT_THE_SHELTER_DOG);
                        } else if (save.get(chatId) == 2) {
                            mailing(chatId, INFORMATION_ABOUT_THE_SHELTER_CAT);
                        }
                    }
                    case "Расписание работы приюта и адрес, схема проезда" -> {
                        if (save.get(chatId) == 1) {
                            mailing(chatId, WORKING_HOURS_DOG);
                        } else if (save.get(chatId) == 2) {
                            mailing(chatId, WORKING_HOURS_CAT);
                        }
                    }
                    case "Рекомендации о технике безопасности на территории приюта" ->
                            mailing(chatId, SECURITY_MEASURES);
                    case "Контактные данные охраны для оформления пропуска на машину" -> mailing(chatId, SECURITY_DATA);
                    case "Позвать волонтера" ->
                            mailing(chatId, "Переадресовываю Ваш запрос волонтеру, пожалуйста, ожидайте");
                    case "Как взять животное из приюта" -> {
                        animalType = save.get(chatId);
                        infoAboutTheAnimalMenu(chatId, animalType);
                    }
                    case "Правила знакомства с животным" -> mailing(chatId, RULES_FOR_GETTING_TO_KNOW_AN_ANIMAL);
                    case "Список документов, необходимых для того, чтобы взять животное из приюта" ->
                            mailing(chatId, LIST_OF_DOCUMENTS);
                    case "Рекомендации по транспортировке животного" -> mailing(chatId, ANIMAL_TRANSPORTATION);
                    case "Рекомендации по обустройству дома щенка", "Рекомендации по обустройству дома котенка" ->
                            mailing(chatId, ANIMAL_ADAPTATION);
                    case "Рекомендации по обустройству дома взрослой собаки", "Рекомендации по обустройству дома взрослого кота/кошки" ->
                            mailing(chatId, ADULT_ANIMAL_ADAPTATION);
                    case "Советы кинолога по первичному общению с собакой" -> mailing(chatId, TIPS_FROM_DOG_HANDLER);
                    case "Рекомендации по проверенным кинологам для дальнейшего обращения к собакой" ->
                            mailing(chatId, RECOMMENDATION_FOR_DOG_HANDLERS);
                    case "Список причин, почему могут отказать в просьбе забрать собаку из приюта", "Список причин, почему могут отказать в просьбе забрать кота/кошку из приюта" ->
                            mailing(chatId, REASON_FOR_REFUSAL);
                    case "Записать контактные данные для связи" ->
                            mailing(chatId, "Пожалуйста, введите сообщение в формате номер телефона + имя. " +
                                    "Например: +7-909-945-4367 Андрей");
                    case "Рекомендаций по обустройству дома собаки с ограниченными возможностями", "Рекомендаций по обустройству дома кота/кошки с ограниченными возможностями" ->
                            mailing(chatId, ADAPTATION_OF_AN_ANIMAL_WITH_DISABILITIES);
                    case "Назад" -> shelterMenu(chatId);
                    case "Прислать отчет о питомце" -> mailing(chatId, INFORMATION_ABOUT_THE_REPORT);
                    default -> mailing(chatId, "Моя твоя не понимать");
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
        if (text.matches(parsePhone)) {
            Pattern pattern = Pattern.compile(parsePhone);
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                String phone = matcher.group(1);
                String name = matcher.group(3);
                UserData userData = new UserData(id, name, phone, save.get(id));
                userRepository.save(userData);
                mailing(id, "Контактные данные сохранены!");
            }
            }
    }

    public void parsingAndSavePhoto(String text, PhotoSize photoSize, Document document, Long id) {
        logger.info("Запуск метода занесения данных для отчета");
        Pattern pattern = Pattern.compile(parseText);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String theAnimalsDiet = matcher.group(1);
            String healthStatus = matcher.group(2);
            String ChangeInBehavior = matcher.group(3);
            UserData userData = new UserData();
            for (int i = 0; i < userRepository.findAll().size(); i++) {
                if (userRepository.findAll().get(i).getIdChat().equals(id)) {
                    userData = userRepository.findAll().get(i);
                }
            }
            LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            Report report = new Report(theAnimalsDiet, healthStatus, ChangeInBehavior, userData, today);
            if (document != null) {
                report.setPhotoOfAnimal(photoOfAnimalService.uploadPhoto(document));
            } else if (photoSize != null) {
                report.setPhotoOfAnimal(photoOfAnimalService.uploadPhoto(photoSize));
            }
            reportRepository.save(report);

            mailing(id, "Отчет сохранен");
            saveText.put(id, null);
            saveDocument.put(id, null);
            savePhotoSize.put(id, null);
        }
    }

    public void savePhotoBd(Long id, PhotoSize[] photo, Document document) {
        if (photo == null && document != null) {
            UserData userData = new UserData();
            for (int i = 0; i < userRepository.findAll().size(); i++) {
                if (userRepository.findAll().get(i).getIdChat().equals(id))
                {userData = userRepository.findAll().get(i);}
            }
            List<Report> reports = new ArrayList<>();
            Report report = new Report();
            for (int i = 0; i < reportRepository.findAll().size(); i++) {
                if (reportRepository.findAll().get(i).equals(userData.getId())) {
                    reports.add(reportRepository.findAll().get(i));
                }
            }
            for (int i = 0; i < reports.size(); i++) {
                if (reports.get(i).getDate().equals(LocalDateTime.now())) {
                    report = reports.get(i);
                }
            }
            report.setPhotoOfAnimal(photoOfAnimalService.uploadPhoto(document));
        } else if (document == null && photo != null) {
            photoOfAnimalService.uploadPhoto(photo[1]);
        }
    }

    /**
     * Данные методы отпраляет сообщение пользователю
     *
     * @param chatId              id чата
     * @param receivedMessage     текст сообщения пользователю
     * @param replyKeyboardMarkup текст сообщения меню.
     */
    public void mailing(long chatId, String receivedMessage, ReplyKeyboardMarkup replyKeyboardMarkup) {
        logger.info("Отправка сообщения");
        SendMessage message = new SendMessage(chatId, receivedMessage).replyMarkup(replyKeyboardMarkup);
        SendResponse response = telegramBot.execute(message);
    }

    public void mailing(long chatId, String receivedMessage) {
        logger.info("Отправка сообщения");
        SendMessage message = new SendMessage(chatId, receivedMessage);
        SendResponse response = telegramBot.execute(message);
    }

    /**
     * Метод для меню приютов для собак и кошек
     *
     * @param chatId id чата
     */
    private void mainMenu(long chatId) {
        logger.info("Запуск меню выбора приюта");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                ("Приют для собак"), "Приют для кошек");
        mailing(chatId, "Добрый день. Наш бот помогает найти новый дом брошенным животным. Пожалуйста, выберете интересующий Вас приют из меню ниже:", replyKeyboardMarkup);
    }

    /**
     * Метод для главного меню
     *
     * @param chatId id чата
     */
    private void shelterMenu(long chatId) {
        logger.info("Запуск основного меню");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{"Узнать информацию о приюте", "Как взять животное из приюта"},
                new String[]{"Прислать отчет о питомце", "Позвать волонтера"},
                new String[]{"Выход"});
        mailing(chatId, "Добрый день. Рады приветствовать Вас в нашем приюте.", replyKeyboardMarkup);
    }

    /**
     * Медот для информационого меню
     *
     * @param chatId - id чата
     */
    private void infoMenu(long chatId) {
        logger.info("Запуск информационного меню");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{"Рассказать о приюте", "Расписание работы приюта и адрес, схема проезда"},
                new String[]{"Рекомендации о технике безопасности на территории приюта", "Записать контактные данные для связи"},
                new String[]{"Контактные данные охраны для оформления пропуска на машину", "Позвать волонтера"},
                new String[]{"Назад"});
        mailing(chatId, "Пожалуйста, выберете интересующую Вас информацию из списка ниже.", replyKeyboardMarkup);
    }

    /**
     * Метод для меню консультации
     *
     * @param chatId     id чата
     * @param animalType - переменая, которая определяет для какго вида животных(собака или кошка) будет запущено меню
     */
    private void infoAboutTheAnimalMenu(long chatId, int animalType) {
        if (animalType == 1) {
            logger.info("Запуск меню собаки");
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                    new String[]{"Правила знакомства с животным", "Список документов, необходимых для того, чтобы взять животное из приюта"},
                    new String[]{"Рекомендации по транспортировке животного", "Рекомендации по обустройству дома щенка"},
                    new String[]{"Рекомендации по обустройству дома взрослой собаки", "Рекомендаций по обустройству дома собаки с ограниченными возможностями"},
                    new String[]{"Советы кинолога по первичному общению с собакой", "Рекомендации по проверенным кинологам для дальнейшего обращения к собакой"},
                    new String[]{"Список причин, почему могут отказать в просьбе забрать собаку из приюта", "Записать контактные данные для связи"},
                    new String[]{"Позвать волонтера", "Назад"});
            mailing(chatId, "Пожалуйста, выберете интересующую Вас информацию из списка ниже.", replyKeyboardMarkup);
        } else if (animalType == 2) {
            logger.info("Запуск меню кота");
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                    new String[]{"Правила знакомства с животным", "Список документов, необходимых для того, чтобы взять животное из приюта"},
                    new String[]{"Рекомендации по транспортировке животного", "Рекомендации по обустройству дома котенка"},
                    new String[]{"Рекомендации по обустройству дома взрослого кота/кошки", "Рекомендаций по обустройству дома кота/кошки с ограниченными возможностями"},
                    new String[]{"Список причин, почему могут отказать в просьбе забрать кота/кошку из приюта", "Записать контактные данные для связи"},
                    new String[]{"Позвать волонтера", "Назад"});
            mailing(chatId, "Пожалуйста, выберете интересующую Вас информацию из списка ниже.", replyKeyboardMarkup);
        }
    }

    /**
     * Метод для заполнения Map(используется в тестирование)
     *
     * @param chatId id чата
     * @param number - Переменная для выбора меню для собаки или кошки
     */
    public void addMapForTests(long chatId, int number) {
        save.put(chatId, number);
    }

    /**
     * Метод возвращает все значения Map(используется в тестирование)
     *
     * @return возврат Map
     */
    public Map<Long, Integer> getAllMapForTests() {
        return new HashMap<>(save);
    }

    /**
     * Метод для атоматического послания сообщений
     *
     */
    @Scheduled(cron = "0 21 * * * *")
    public void warning() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        userRepository.findAll().stream()
                .map(UserData::getReports)
                .map(reports ->
                        reports.stream()
                                .reduce((first, second) -> second)
                                .orElse(null))
                .filter(Objects::nonNull)
                .filter(report -> ChronoUnit.DAYS.between(report.getDate(), now) == 1 || ChronoUnit.DAYS.between(report.getDate(), now) == 2)
                .map(Report::getUserData)
                .forEach(user -> telegramBot.execute(new SendMessage(user.getIdChat(), "Сделайте отчет")));

        userRepository.findAll().stream()
                .map(UserData::getReports)
                .map(reports ->
                        reports.stream()
                                .reduce((first, second) -> second)
                                .orElse(null))
                .filter(Objects::nonNull)
                .filter(report -> ChronoUnit.DAYS.between(report.getDate(), now) > 2)
                .map(Report::getUserData)
                .map(UserData::getAnimal)
                .forEach(animal -> telegramBot.execute(new SendMessage(animal.getVolunteer().getIdChat(), "Пользователь с животным id - " + animal.getId() + " не делает отчеты больше 2-х дней")));

        userRepository.findAll().stream()
                .filter(user -> user.getDate().toLocalDate().equals(now.toLocalDate()))
                .map(UserData::getAnimal)
                .forEach(animal -> telegramBot.execute(new SendMessage(animal.getVolunteer().getIdChat(), "У пользователя с животным id - " + animal.getId() + "закончился испытательный период")));
    }
}
