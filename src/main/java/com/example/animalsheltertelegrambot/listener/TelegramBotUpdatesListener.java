package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.PhotoOfAnimal;
import com.example.animalsheltertelegrambot.model.Report;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.model.Volunteer;
import com.example.animalsheltertelegrambot.repository.ReportRepository;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.example.animalsheltertelegrambot.repository.VolunteerRepository;
import com.example.animalsheltertelegrambot.service.PhotoOfAnimalService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    /**
     * регулярное выражение парсинга строки для текста отчета
     */
    private final String parseText = "([\\W+]+)/([\\W+]+)/([\\W+]+)";
    /**
     * регулярное выражение парсинга строки для занесения волонетру id чата
     */
    private final String parseIdText = "(([П][р][и][м][и]\\s[м][е][н][я])-([\\d+]+))";
    /**
     * регулярное выражение парсинга строки для ответа на сообщение пользователю через чат бота
     */
    private final String parseResponse = "(([\\d+]+)\\s=\\s([\\W+]+))";
    /**
     * Хранение значения, для разделения приютов для собак и кошек
     */
    private final Map<Long, Integer> save = new HashMap<>();
    /**
     * Хранение значения, для открытия/закрытия чата
     */
    private final Map<Long, Integer> answer = new HashMap<>();
    /**
     * Хранение фото для отчета
     */
    private final Map<Long, PhotoOfAnimal> photo = new HashMap<>();
    /**
     * Хранение строки отчета
     */
    private final Map<Long, String> report = new HashMap<>();
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
    private final PhotoOfAnimalService photoOfAnimalService;
    /**
     * Обьяление репозитория отчетов
     */
    private final ReportRepository reportRepository;
    /**
     * Обьявление репозитория волонетров
     */
    private final VolunteerRepository volunteerRepository;

    /**
     * Инжектим бота + репозиторий
     *
     * @param telegramBot          бот
     * @param userRepository       репозиторий
     * @param photoOfAnimalService обработка фотографии животного
     * @param reportRepository     репозиторий
     * @param volunteerRepository  репозиторий
     */
    public TelegramBotUpdatesListener(TelegramBot telegramBot, UserRepository userRepository, PhotoOfAnimalService photoOfAnimalService,
                                      ReportRepository reportRepository,
                                      VolunteerRepository volunteerRepository) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
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
            Integer animalType;
            int confirmation = 0;
            if (update.message() != null && update.message().photo() == null && update.message().document() == null && text.matches(parsePhone)) {
                userVerification(text, chatId);
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null && text.matches(parseResponse)) {
                responseToTheUser(text);
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null && text.matches(parseIdText)) {
                setVolunteerChatId(text, chatId);
            } else if (update.message() != null && (update.message().photo() != null || update.message().document() != null || text.matches(parseText))) {
                if (update.message().photo() != null) {
                    photo.put(chatId, photoOfAnimalService.uploadPhoto(update.message().photo()));
                } else if (update.message().document() != null) {
                    photo.put(chatId, photoOfAnimalService.uploadPhoto(update.message().document()));
                } else {
                    report.put(chatId, text);
                }
                checkReport(chatId);
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null) {
                switch (text) {
                    case START -> mainMenu(chatId);
                    case EXIT -> mainMenu(chatId);
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
                        try {
                            if (save.get(chatId) == 1) {
                                mailing(chatId, INFORMATION_ABOUT_THE_SHELTER_DOG);
                            } else if (save.get(chatId) == 2) {
                                mailing(chatId, INFORMATION_ABOUT_THE_SHELTER_CAT);
                            }
                        } catch (NullPointerException e) {
                            mainMenu(chatId);
                        }
                    }
                    case JOB_DESCRIPTION -> {
                        try {
                            if (save.get(chatId) == 1) {
                                mailing(chatId, WORKING_HOURS_DOG);
                            } else if (save.get(chatId) == 2) {
                                mailing(chatId, WORKING_HOURS_CAT);
                            }
                        } catch (NullPointerException e) {
                            mainMenu(chatId);
                        }
                    }
                    case SAFETY ->
                            mailing(chatId, SECURITY_MEASURES);
                    case SECURITY -> mailing(chatId, SECURITY_DATA);
                    case CALLING_A_VOLUNTEER -> callingVolunteer(chatId);
                    case START_A_CHAT -> {
                        confirmation = 1;
                        answer.put(chatId, confirmation);
                        mailing(chatId, USER_QUESTION);
                    }
                    case CLOSE_THE_CHAT ->{
                        answer.remove(chatId);
                        shelterMenu(chatId);
                    }
                    case BACK -> shelterMenu(chatId);
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
                    case SEND_REPORT -> mailing(chatId, INFORMATION_ABOUT_THE_REPORT);
                    default ->{
                        if (answer.get(chatId) != null) {
                            messageToTheVolunteer(chatId, text);
                        } else {
                            mailing(chatId, STANDARD_RESPONSE);
                        }
                    }
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
            UserData userData = new UserData(id, name, phone, save.get(id));
            userRepository.save(userData);
            mailing(id, "Контактные данные сохранены!");
        }
    }

    /**
     * @param text строка содержащее кодовое слово и id волонтера
     * @param id id чата
     */
    public void setVolunteerChatId(String text, Long id){
        logger.info("Запись ID волонтеру");
            Pattern pattern = Pattern.compile(parseIdText);
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                Long idVolunteer = Long.valueOf(matcher.group(3));
                Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
                try {
                    Volunteer volunteer1 = volunteer.get();
                    volunteer1.setChatId(id);
                    volunteerRepository.save(volunteer1);
                    mailing(id, "ID присвоен");
                } catch (NoSuchElementException e) {
                    mailing(id, "Волонтер не найден");
                }
            }
    }

    /**
     * Метод проверки повторной регистрации
     * @param text данные пользователя
     * @param id id чата
     */
    public void userVerification(String text, Long id) {
        logger.info("Проверка пользователя");
        UserData userData = userRepository.findByChatId(id);
        if (userData == null) {
            parsing(text, id);
        } else {
            mailing(id, "Вы уже внесли контактные данные");
        }
    }

    /**
     * Метод по отправке сообщения волонтеру
     * @param chatId id чата
     * @param text сообщение пользователя
     */
    public void messageToTheVolunteer(Long chatId, String text) {
        logger.info("Отправка сообщения волонтеру");
        Random random = new Random();
        List<Long> volunteerChatId = volunteerRepository.findAll().stream().map(Volunteer::getChatId).filter(Objects::nonNull).toList();
        if (volunteerChatId.isEmpty()) {
            mailing(chatId,"Волонтеры заняты");
        } else {
            mailing(volunteerChatId.get(random.nextInt(volunteerChatId.size())), "Пользователь - " + chatId + " задал вопрос : " + text);
        }
    }

    /**
     * Метод по отправке сообщения пользователю
     * @param text ответ волонтера
     */
    public void responseToTheUser(String text) {
        logger.info("Отправка сообщения пользователю");
        Pattern pattern = Pattern.compile(parseResponse);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            long id = Long.parseLong(matcher.group(2));
            String answer = matcher.group(3);
            mailing(id, "Ответ волонтера: " + answer);
        }
    }

    /**
     * Метод заполнения отчета
     * @param text текст отчета
     * @param photoOfAnimal фотография животного
     * @param chatId id чата
     */
    public void parsing(String text, PhotoOfAnimal photoOfAnimal, long chatId) {
        logger.info("Заполнение отчета");
        Pattern pattern = Pattern.compile(parseText);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String theAnimalsDiet = matcher.group(1);
            String healthStatus = matcher.group(2);
            String changeInBehavior = matcher.group(3);
            UserData userData = userRepository.findByChatId(chatId);
            LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            Report report = new Report(theAnimalsDiet, healthStatus, changeInBehavior, userData, dateTime, photoOfAnimal);
            reportRepository.save(report);
            cleanParameters(chatId);
            mailing(chatId, "Отчет создан");
        }
    }

    /**
     * Метод проверки данных для отчета
     * @param chatId id чата
     */
    public void checkReport(long chatId) {
        if (photo.get(chatId) == null) {
            mailing(chatId, UPLOAD_PHOTO);
        }
        if (report.get(chatId) == null) {
            mailing(chatId, LOADING_THE_REPORT);
        }
        if (photo.get(chatId) != null && report.get(chatId) != null) {
            parsing(report.get(chatId), photo.get(chatId), chatId);
        }
    }

    /**
     * Метод очистки мапы
     * @param chatId id чата
     */
    public void cleanParameters(long chatId) {
        photo.remove(chatId);
        report.remove(chatId);
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
     * Метод вызова чата с волонетром
     * @param chatId id чата
     */
    private void callingVolunteer(long chatId) {
        logger.info("Запуск меню подтверждения вызова волонтера");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                START_A_CHAT, CLOSE_THE_CHAT);
                mailing(chatId, "Хотите задать вопрос волонтеру?", replyKeyboardMarkup);
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
    @Transactional(readOnly = true)
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
                    .forEach(user -> telegramBot.execute(new SendMessage(user.getChatId(), "Сделайте отчет")));


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
                .forEach(animal -> telegramBot.execute(new SendMessage(animal.getVolunteer().getChatId(), "Пользователь с животным id - " + animal.getId() + " не делает отчеты больше 2-х дней")));

        userRepository.findAll().stream()
                .filter(user -> user.getDate() != null && user.getDate().toLocalDate().equals(now.toLocalDate()))
                .map(UserData::getAnimal)
                .forEach(animal -> telegramBot.execute(new SendMessage(animal.getVolunteer().getChatId(), "У пользователя с животным id - " + animal.getId() + "закончился испытательный период")));
    }
}
