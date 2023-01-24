package com.example.animalsheltertelegrambot.listener;

import com.example.animalsheltertelegrambot.model.*;
import com.example.animalsheltertelegrambot.repository.ParametersRepository;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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
     * Объявление репозитория параметров
     */
    private final ParametersRepository parametersRepository;
    private Pattern pattern;
    private Random random = new Random();

    /**
     * Инжектим бота + репозиторий
     *
     * @param telegramBot          бот
     * @param userRepository       репозиторий пользователей
     * @param photoOfAnimalService обработка фотографии животного
     * @param reportRepository     репозиторий отчетов
     * @param volunteerRepository  репозиторий волонтеров
     * @param parametersRepository реаозиторий параметров
     */
    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      UserRepository userRepository,
                                      PhotoOfAnimalService photoOfAnimalService,
                                      ReportRepository reportRepository,
                                      VolunteerRepository volunteerRepository,
                                      ParametersRepository parametersRepository) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.photoOfAnimalService = photoOfAnimalService;
        this.volunteerRepository = volunteerRepository;
        this.parametersRepository = parametersRepository;
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
            if (update.message() != null && update.message().photo() == null && update.message().document() == null &&
                    (update.message().sticker() != null || update.message().audio() != null || update.message().voice() != null || update.message().video() != null
                    || update.message().videoNote()!= null)) {
                mailing(chatId, GET_STICKER);
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null && text.matches(parsePhone)) {
                    userVerification(text, chatId);
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null && text.matches(parseResponse)) {
                    responseToTheUser(text);
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null && text.matches(parseIdText)) {
                    setVolunteerChatId(text, chatId);
            } else if (update.message() != null && (update.message().photo() != null || update.message().document() != null || text.matches(parseText))) {
                    createReport(update.message().photo(), update.message().document(), text, chatId);
            } else if (update.message() != null && update.message().photo() == null && update.message().document() == null) {
                switch (text) {
                    case START, EXIT -> mainMenu(chatId);
                    case DOG_SHELTER -> {
                        parametersRepository.save(new Parameters(chatId, 1));
                        shelterMenu(chatId);
                    }
                    case CAT_SHELTER -> {
                        parametersRepository.save(new Parameters(chatId, 2));
                        shelterMenu(chatId);
                    }
                    case INFORMATION_SHELTER -> infoMenu(chatId);
                    case STORY_SHELTER -> {
                        Parameters parameters = parameters(chatId);
                        if (parameters != null) {
                            if (parameters.getShelter() == 1) {
                                mailing(chatId, INFORMATION_ABOUT_THE_SHELTER_DOG);
                            } else if (parameters.getShelter() == 2) {
                                mailing(chatId, INFORMATION_ABOUT_THE_SHELTER_CAT);
                            }
                        } else {
                            mainMenu(chatId);
                        }
                    }
                    case JOB_DESCRIPTION -> {
                        Parameters parameters = parameters(chatId);
                        if (parameters != null) {
                            if (parameters.getShelter() == 1) {
                                mailing(chatId, WORKING_HOURS_DOG);
                            } else if (parameters.getShelter() == 2) {
                                mailing(chatId, WORKING_HOURS_CAT);
                            }
                        } else {
                            mainMenu(chatId);
                        }
                    }
                        case SAFETY -> mailing(chatId, SECURITY_MEASURES);
                        case SECURITY -> mailing(chatId, SECURITY_DATA);
                        case CALLING_A_VOLUNTEER -> {
                            Parameters parameters = parameters(chatId);
                            if (parameters != null) {
                                parameters.setChat(1);
                                parametersRepository.save(parameters);
                                callingVolunteer(chatId);
                            } else {
                                mainMenu(chatId);
                            }
                        }
                        case CLOSE_THE_CHAT -> {
                            Parameters parameters = parameters(chatId);
                            if (parameters != null) {
                                parameters.setChat(0);
                                parametersRepository.save(parameters);
                                shelterMenu(chatId);
                            } else {
                                mainMenu(chatId);
                            }
                        }
                        case BACK -> shelterMenu(chatId);
                        case TAKE_AN_ANIMAL_FROM_A_SHELTER -> {
                            Parameters parameters = parameters(chatId);
                            if (parameters != null) {
                                infoAboutTheAnimalMenu(chatId, parameters.getShelter());
                            } else {
                                mainMenu(chatId);
                            }
                        }
                        case RULES_OF_ACQUAINTANCE_WITH_ANIMALS -> mailing(chatId, RULES_FOR_GETTING_TO_KNOW_AN_ANIMAL);
                        case LIST_OF_DOCUMENTS_FOR_ANIMALS -> mailing(chatId, LIST_OF_DOCUMENTS);
                        case TRANSPORTATION_RECOMMENDATION -> mailing(chatId, ANIMAL_TRANSPORTATION);
                        case ARRANGEMENT_OF_THE_PUPPY, ARRANGEMENT_OF_THE_KITTEN -> mailing(chatId, ANIMAL_ADAPTATION);
                        case ARRANGEMENT_OF_THE_DOG, ARRANGEMENT_OF_THE_CAT -> mailing(chatId, ADULT_ANIMAL_ADAPTATION);
                        case RECOMMENDATIONS -> mailing(chatId, TIPS_FROM_DOG_HANDLER);
                        case RECOMMENDATIONS_DOG_HANDLER -> mailing(chatId, RECOMMENDATION_FOR_DOG_HANDLERS);
                        case REASONS_FOR_REFUSAL_DOG, REASONS_FOR_REFUSAL_CAT -> mailing(chatId, REASON_FOR_REFUSAL);
                        case RECORDING_CONTACT_DETAILS -> {
                            Parameters parameters = parameters(chatId);
                            if (parameters != null) {
                                parameters.setAdd(1);
                                parametersRepository.save(parameters);
                                menuAddUser(chatId);
                            } else {
                                mainMenu(chatId);
                            }
                        }
                        case RECOMMENDATIONS_DISABLED_DOG, RECOMMENDATIONS_DISABLED_CAT ->
                                mailing(chatId, ADAPTATION_OF_AN_ANIMAL_WITH_DISABILITIES);
                        case SEND_REPORT -> {
                            UserData userData = userRepository.findAll().stream()
                                    .filter(user -> Objects.equals(user.getChatId(), chatId) && user.getAnimal() != null)
                                    .findFirst()
                                    .orElse(null);
                            if (userData != null) {
                                Parameters parameters = parameters(chatId);
                                if (parameters != null) {
                                    parameters.setReport(1);
                                    parametersRepository.save(parameters);
                                    menuReport(chatId);
                                } else {
                                    mainMenu(chatId);
                                }
                            } else {
                                mailing(chatId, FIND_USER);
                            }
                        }
                        case CLOSE_THE_REPORT -> cleanParameters(chatId);
                        case CLOSE_THE_ADD_USER -> {
                            Parameters parameters = parameters(chatId);
                            if (parameters != null) {
                                parameters.setAdd(0);
                                parametersRepository.save(parameters);
                                shelterMenu(chatId);
                            } else {
                                mainMenu(chatId);
                            }
                        }
                        default -> {
                            Parameters parameters = parameters(chatId);
                            if (parameters != null) {
                                if (parameters.getChat() == 1) {
                                    messageToTheVolunteer(chatId, text);
                                } else if (parameters.getAdd() == 1) {
                                    mailing(chatId, STANDARD4_RESPONSE);
                                } else if (parameters.getReport() == 1) {
                                    mailing(chatId, STANDARD3_RESPONSE);
                                } else {
                                    mailing(chatId, STANDARD_RESPONSE);
                                }
                            } else {
                                mainMenu(chatId);
                            }
                        }
                    }
                }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

//    mailing(chatId, GET_STICKER);

    /**
     * Метод парсит стороку и заносит информацию в БД
     *
     * @param text сообщение пользователя содержащее номер телефона + имя
     * @param id   id чата
     */
    public void parsing(String text, Long id) {
        logger.info("Парсинг");
        pattern = Pattern.compile(parsePhone);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String phone = matcher.group(1);
            String name = matcher.group(3);
            UserData userData = new UserData(id, name, phone, parameters(id).getShelter());
            userRepository.save(userData);
            mailing(id, "Контактные данные сохранены!");

        }

    }

    /**
     * Метод заполнения отчета
     *
     * @param text          текст отчета
     * @param photoOfAnimal фотография животного
     * @param chatId        id чата
     */
    public void parsing(String text, PhotoOfAnimal photoOfAnimal, long chatId) {
        logger.info("Заполнение отчета");
        pattern = Pattern.compile(parseText);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String theAnimalsDiet = matcher.group(1);
            String healthStatus = matcher.group(2);
            String changeInBehavior = matcher.group(3);
            UserData userData = userRepository.findByChatId(chatId);
            LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            Report report = new Report(theAnimalsDiet, healthStatus, changeInBehavior, userData, dateTime, photoOfAnimal);
            reportRepository.save(report);
            mailing(chatId, "Отчет создан");
            cleanParameters(chatId);
        }
    }

    /**
     * @param text строка содержащее кодовое слово и id волонтера
     * @param id   id чата
     */
    public void setVolunteerChatId(String text, Long id) {
        logger.info("Запись ID волонтеру");
        pattern = Pattern.compile(parseIdText);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            Long idVolunteer = Long.valueOf(matcher.group(3));
            Volunteer volunteer = volunteerRepository.findById(idVolunteer).orElse(null);
            if (volunteer != null) {
                volunteer.setChatId(id);
                volunteerRepository.save(volunteer);
                mailing(id, "ID присвоен");
            } else {
                mailing(id, "Волонтер не найден");
            }
        }
    }

    /**
     * Метод проверки повторной регистрации
     *
     * @param text данные пользователя
     * @param id   id чата
     */
    public void userVerification(String text, Long id) {
        logger.info("Проверка пользователя");
        Parameters parameters = parameters(id);
        if (parameters != null) {
            UserData userData = userRepository.findByChatId(id);
            if (userData == null) {
                parsing(text, id);
            } else {
                mailing(id, "Вы уже внесли контактные данные");
            }
            parameters.setAdd(0);
            parametersRepository.save(parameters);
            shelterMenu(id);
        } else {
            mainMenu(id);
        }
    }

    /**
     * Метод по отправке сообщения волонтеру
     *
     * @param chatId id чата
     * @param text   сообщение пользователя
     */
    public void messageToTheVolunteer(Long chatId, String text) {
        logger.info("Отправка сообщения волонтеру");
        List<Long> volunteerChatId = volunteerRepository.findAll().stream().map(Volunteer::getChatId).filter(Objects::nonNull).toList();
        if (volunteerChatId.isEmpty()) {
            mailing(chatId, "Волонтеры заняты");
        } else {
            mailing(volunteerChatId.get(random.nextInt(volunteerChatId.size())), "Пользователь - " + chatId + " задал вопрос : " + text);
        }
    }

    /**
     * Метод для генерации отчета
     *
     * @param photoSizes сжатая фотография
     * @param document   фотография полного размера
     * @param text       текст отчета
     * @param chatId     id чата
     */
    public void createReport(PhotoSize[] photoSizes, Document document, String text, long chatId) {
        Parameters parameters = parameters(chatId);
        if (parameters != null) {
            if (parameters.getReport() == 1) {
                if (photoSizes != null) {
                    parameters.setPhotoOfAnimal(photoOfAnimalService.uploadPhoto(photoSizes));
                    parametersRepository.save(parameters);
                } else if (document != null) {
                    parameters.setPhotoOfAnimal(photoOfAnimalService.uploadPhoto(document));
                    if (parameters.getPhotoOfAnimal() == null) {
                        mailing(chatId, "Неправильный формат. Пришлите фотографию с форматом jpeg или png");
                        return;
                    } else {
                        parametersRepository.save(parameters);
                    }
                } else {
                    parameters.setText(text);
                    parametersRepository.save(parameters);
                }
                checkReport(chatId);
            } else {
                mailing(chatId, STANDARD2_RESPONSE);
            }
        } else {
            mainMenu(chatId);
        }
    }

    /**
     * Метод по отправке сообщения пользователю
     *
     * @param text ответ волонтера
     */
    public void responseToTheUser(String text) {
        logger.info("Отправка сообщения пользователю");
        pattern = Pattern.compile(parseResponse);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            long id = Long.parseLong(matcher.group(2));
            String answer = matcher.group(3);
            mailing(id, "Ответ волонтера: " + answer);
        }
    }


    /**
     * Метод проверки данных для отчета
     *
     * @param chatId id чата
     */
    public void checkReport(long chatId) {
        if (parameters(chatId).getPhotoOfAnimal() == null) {
            mailing(chatId, UPLOAD_PHOTO);
        }
        if (parameters(chatId).getText() == null) {
            mailing(chatId, LOADING_THE_REPORT);
        }
        if (parameters(chatId).getPhotoOfAnimal() != null && parameters(chatId).getText() != null) {
            parsing(parameters(chatId).getText(), parameters(chatId).getPhotoOfAnimal(), chatId);
        }
    }

    /**
     * Метод очистки мапы
     *
     * @param chatId id чата
     */
    public void cleanParameters(long chatId) {
        Parameters parameters = parameters(chatId);
        parameters.setPhotoOfAnimal(null);
        parameters.setText(null);
        parameters.setReport(0);
        parametersRepository.save(parameters);
        shelterMenu(chatId);
    }

    public Parameters parameters(long chatId) {
        return parametersRepository.findById(chatId).orElse(null);
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
     *
     * @param chatId id чата
     */
    private void callingVolunteer(long chatId) {
        logger.info("Запуск меню подтверждения вызова волонтера");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(CLOSE_THE_CHAT);
        mailing(chatId, USER_QUESTION, replyKeyboardMarkup);
    }

    private void menuReport(long chatId) {
        logger.info("Запуск меню отчета");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(CLOSE_THE_REPORT);
        mailing(chatId, INFORMATION_ABOUT_THE_REPORT, replyKeyboardMarkup);
    }

    private void menuAddUser(long chatId) {
        logger.info("Запуск меню добавления пользователя");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(CLOSE_THE_ADD_USER);
        mailing(chatId, EXAMPLE_OF_A_MESSAGE, replyKeyboardMarkup);
    }
}
