package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.Report;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.model.Volunteer;
import com.example.animalsheltertelegrambot.repository.PhotoRepository;
import com.example.animalsheltertelegrambot.repository.ReportRepository;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.example.animalsheltertelegrambot.repository.VolunteerRepository;
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
    private final String parseIdText = "(([П][р][и][м][и]\\s[м][е][н][я])-([\\d+]+))";
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
    private final VolunteerRepository volunteerRepository;
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
                                      PhotoRepository photoRepository, VolunteerRepository volunteerRepository) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.photoRepository = photoRepository;
        this.photoOfAnimalService = photoOfAnimalService;
        this.volunteerRepository = volunteerRepository;
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
            String text = update.message().text();
            Long chatId = update.message().chat().id();
            Document document = update.message().document();
            PhotoSize[] photo = update.message().photo();
            Integer animalType = 0;
            if (update.message() != null && photo == null && document == null && text.matches(parsePhone)) {
                userVerification(text, chatId);
            } else if (update.message() != null && photo == null && document == null && text.matches(parseIdText)) {
                setVolunteerChatId(text, chatId);
            } else if (update.message() != null && photo == null && document == null && text.matches(parseText)
                    && (saveDocument.get(chatId) == null && savePhotoSize.get(chatId) == null)) {
                saveText.put(chatId, text);
                mailing(chatId, UPLOAD_PHOTO);
            } else if (update.message() != null && (photo != null || document != null) && text == null && saveText.get(chatId) == null) {
                if (document != null) {
                    saveDocument.put(chatId, document);
                    mailing(chatId, LOADING_THE_REPORT);
                } else if (photo[1] != null) {
                    savePhotoSize.put(chatId, photo[1]);
                    mailing(chatId, LOADING_THE_REPORT);
                }
            } else if (update.message() != null && (savePhotoSize.get(chatId) != null || saveDocument.get(chatId) != null) && text != null && text.matches(parseText)
            && document == null && photo == null) {
                saveText.put(chatId, text);
                parsing(saveText.get(chatId), savePhotoSize.get(chatId), saveDocument.get(chatId), chatId);
            } else if (update.message() != null && photo == null && document == null && text != null && text.matches(parseText) &&
                    (saveDocument.get(chatId) != null || savePhotoSize.get(chatId) != null)) {
                saveText.put(chatId, text);
                parsing(saveText.get(chatId), savePhotoSize.get(chatId), saveDocument.get(chatId), chatId);
            } else if (update.message() != null && (photo != null || document != null) && text == null && saveText.get(chatId) != null &&
                    saveDocument.get(chatId) == null && savePhotoSize.get(chatId) == null) {
                if (document != null) {
                    saveDocument.put(chatId, document);
                    parsing(saveText.get(chatId), savePhotoSize.get(chatId), saveDocument.get(chatId), chatId);
                } else if (photo[1] != null) {
                    savePhotoSize.put(chatId, photo[1]);
                    parsing(saveText.get(chatId), savePhotoSize.get(chatId), saveDocument.get(chatId), chatId);
                }
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null && text != null) {
                switch (text) {
                    case START, EXIT -> mainMenu(chatId);
                    case DOG_SHELTER -> {
                        animalType = 1;
                        save.put(chatId, animalType);
                        shelterMenu(chatId);
                    }
                    case CAT_SHELTER -> {
                        animalType = 2;
                        save.put(chatId, animalType);
                        shelterMenu(chatId);
                    }
                    case INFORMATION_SHELTER -> infoMenu(chatId);
                    case STORY_SHELTER -> {
                        if (save.get(chatId) == 1) {
                            mailing(chatId, INFORMATION_ABOUT_THE_SHELTER_DOG);
                        } else if (save.get(chatId) == 2) {
                            mailing(chatId, INFORMATION_ABOUT_THE_SHELTER_CAT);
                        }
                    }
                    case JOB_DESCRIPTION -> {
                        if (save.get(chatId) == 1) {
                            mailing(chatId, WORKING_HOURS_DOG);
                        } else if (save.get(chatId) == 2) {
                            mailing(chatId, WORKING_HOURS_CAT);
                        }
                    }
                    case SAFETY ->
                            mailing(chatId, SECURITY_MEASURES);
                    case SECURITY -> mailing(chatId, SECURITY_DATA);
                    case CALLING_A_VOLUNTEER ->
                            mailing(chatId, "Переадресовываю Ваш запрос волонтеру, пожалуйста, ожидайте");
                    case TAKE_AN_ANIMAL_FROM_A_SHELTER -> {
                        animalType = save.get(chatId);
                        infoAboutTheAnimalMenu(chatId, animalType);
                    }
                    case RULES_OF_ACQUAINTANCE_WITH_ANIMALS -> mailing(chatId, RULES_FOR_GETTING_TO_KNOW_AN_ANIMAL);
                    case LIST_OF_DOCUMENTS_FOR_ANIMALS ->
                            mailing(chatId, LIST_OF_DOCUMENTS);
                    case TRANSPORTATION_RECOMMENDATION -> mailing(chatId, ANIMAL_TRANSPORTATION);
                    case ARRANGEMENT_OF_THE_PUPPY, ARRANGEMENT_OF_THE_KITTEN ->
                            mailing(chatId, ANIMAL_ADAPTATION);
                    case ARRANGEMENT_OF_THE_DOG, ARRANGEMENT_OF_THE_CAT ->
                            mailing(chatId, ADULT_ANIMAL_ADAPTATION);
                    case RECOMMENDATIONS -> mailing(chatId, TIPS_FROM_DOG_HANDLER);
                    case RECOMMENDATIONS_DOG_HANDLER ->
                            mailing(chatId, RECOMMENDATION_FOR_DOG_HANDLERS);
                    case REASONS_FOR_REFUSAL_DOG, REASONS_FOR_REFUSAL_CAT ->
                            mailing(chatId, REASON_FOR_REFUSAL);
                    case RECORDING_CONTACT_DETAILS ->
                            mailing(chatId, EXAMPLE_OF_A_MESSAGE);
                    case RECOMMENDATIONS_DISABLED_DOG, RECOMMENDATIONS_DISABLED_CAT ->
                            mailing(chatId, ADAPTATION_OF_AN_ANIMAL_WITH_DISABILITIES);
                    case BACK -> shelterMenu(chatId);
                    case SEND_REPORT -> mailing(chatId, INFORMATION_ABOUT_THE_REPORT);
                    default -> mailing(chatId, CALLING_A_VOLUNTEER);
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

    public void setVolunteerChatId(String text, Long id) {
        logger.info("Запись ID волонтеру");
        if (text.matches(parseIdText)) {
            Pattern pattern = Pattern.compile(parseIdText);
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                Long idVolunteer = Long.valueOf(matcher.group(3));
                Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
                Volunteer volunteer1 = volunteer.get();
                volunteer1.setIdChat(id);
                volunteerRepository.save(volunteer1);
                mailing(id, "ChatId присвоен");
            }
        }
    }

    public void userVerification(String text, Long id) {
        logger.info("Проверка пользователя");
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            if (userRepository.findAll().get(i).getIdChat().equals(id)) {
                mailing(id, "Вы уже внесли контактные данные");
                break;
            } else {
                parsing(text, id);
            }
        }
    }

    public void parsing(String text, PhotoSize photoSize, Document document, Long id) {
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
            clearing(id);
        }
    }

    public void clearing(Long id) {
        logger.info("Данные в мапе очищены");
        saveText.put(id, null);
        saveDocument.put(id, null);
        savePhotoSize.put(id, null);
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
                (DOG_SHELTER), CAT_SHELTER);
        mailing(chatId, GREETING, replyKeyboardMarkup);
    }

    /**
     * Метод для главного меню
     *
     * @param chatId id чата
     */
    private void shelterMenu(long chatId) {
        logger.info("Запуск основного меню");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{INFORMATION_SHELTER, TAKE_AN_ANIMAL_FROM_A_SHELTER},
                new String[]{SEND_REPORT, CALLING_A_VOLUNTEER},
                new String[]{EXIT});
        mailing(chatId, GREETING_SHELTER, replyKeyboardMarkup);
    }

    /**
     * Медот для информационого меню
     *
     * @param chatId - id чата
     */
    private void infoMenu(long chatId) {
        logger.info("Запуск информационного меню");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{STORY_SHELTER, JOB_DESCRIPTION},
                new String[]{SAFETY, RECORDING_CONTACT_DETAILS},
                new String[]{SECURITY, CALLING_A_VOLUNTEER},
                new String[]{BACK});
        mailing(chatId, MENU_SELECTION, replyKeyboardMarkup);
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
                    new String[]{RULES_OF_ACQUAINTANCE_WITH_ANIMALS, LIST_OF_DOCUMENTS_FOR_ANIMALS},
                    new String[]{TRANSPORTATION_RECOMMENDATION, ARRANGEMENT_OF_THE_PUPPY},
                    new String[]{ARRANGEMENT_OF_THE_DOG, RECOMMENDATIONS_DISABLED_DOG},
                    new String[]{RECOMMENDATIONS, RECOMMENDATIONS_DOG_HANDLER},
                    new String[]{REASONS_FOR_REFUSAL_DOG, RECORDING_CONTACT_DETAILS},
                    new String[]{CALLING_A_VOLUNTEER, BACK});
            mailing(chatId, MENU_SELECTION, replyKeyboardMarkup);
        } else if (animalType == 2) {
            logger.info("Запуск меню кота");
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                    new String[]{RULES_OF_ACQUAINTANCE_WITH_ANIMALS, LIST_OF_DOCUMENTS_FOR_ANIMALS},
                    new String[]{TRANSPORTATION_RECOMMENDATION, ARRANGEMENT_OF_THE_KITTEN},
                    new String[]{ARRANGEMENT_OF_THE_CAT, RECOMMENDATIONS_DISABLED_CAT},
                    new String[]{REASONS_FOR_REFUSAL_CAT, RECORDING_CONTACT_DETAILS},
                    new String[]{CALLING_A_VOLUNTEER, BACK});
            mailing(chatId, MENU_SELECTION, replyKeyboardMarkup);
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
