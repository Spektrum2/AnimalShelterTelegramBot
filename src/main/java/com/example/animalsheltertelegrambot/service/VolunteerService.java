package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.component.RecordMapper;
import com.example.animalsheltertelegrambot.exception.*;
import com.example.animalsheltertelegrambot.listener.TelegramBotUpdatesListener;
import com.example.animalsheltertelegrambot.model.Animal;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.model.Volunteer;
import com.example.animalsheltertelegrambot.record.AnimalRecord;
import com.example.animalsheltertelegrambot.record.ReportRecord;
import com.example.animalsheltertelegrambot.record.UserRecord;
import com.example.animalsheltertelegrambot.record.VolunteerRecord;
import com.example.animalsheltertelegrambot.repository.AnimalRepository;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.example.animalsheltertelegrambot.repository.VolunteerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VolunteerService {
    private final Logger logger = LoggerFactory.getLogger(VolunteerService.class);
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final RecordMapper recordMapper;
    private final TelegramBotUpdatesListener telegramBotUpdatesListener;

    public VolunteerService(VolunteerRepository volunteerRepository,
                            UserRepository userRepository,
                            AnimalRepository animalRepository,
                            RecordMapper recordMapper, TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.volunteerRepository = volunteerRepository;
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
        this.recordMapper = recordMapper;
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }

    /**
     * Метод находит всех волонтеров в БД
     *
     * @return возвращает список волонтеров
     */
    public Collection<VolunteerRecord> getAllVolunteers() {
        logger.info("Was invoked method for get all volunteers");
        return volunteerRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());

    }

    /**
     * Метод находит всех пользователей в БД
     *
     * @return возвращает список пользователей
     */
    public Collection<UserRecord> getAllUsers() {
        logger.info("Was invoked method for get all users");
        return userRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    /**
     * Метод находит волонтера по id
     *
     * @param id id волонтера
     * @return возвращает найденного волонтера
     */
    public VolunteerRecord findVolunteer(Long id) {
        logger.info("Was invoked method  for find volunteer");
        return recordMapper.toRecord(volunteerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not volunteer with id = {}", id);
                    return new VolonteerNotFoundException(id);
                }));
    }

    /**
     * Метод находит пользователя по id
     *
     * @param id id пользователя
     * @return возвращает найденного пользователя
     */
    public UserRecord findUser(Long id) {
        logger.info("Was invoked method  for find user");
        return recordMapper.toRecord(userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not user with id = {}", id);
                    return new UserNotFoundException(id);
                }));
    }

    /**
     * Метод создает волантера в БД
     *
     * @param volunteerRecord волантер
     * @return возвращает созданного волонтера
     */
    public VolunteerRecord createVolunteer(VolunteerRecord volunteerRecord) {
        logger.info("Was invoked method for create volunteer");
        Volunteer volunteer = recordMapper.toEntity(volunteerRecord);
        return recordMapper.toRecord(volunteerRepository.save(volunteer));
    }

    /**
     * Метод удаляет волонтера из БД
     *
     * @param id id волонтера
     * @return возвращает удаленного волонтера
     */
    public VolunteerRecord deleteVolunteer(Long id) {
        logger.info("Was invoked method for delete animal");
        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not volunteer with id = {}", id);
                    return new VolonteerNotFoundException(id);
                });
        volunteerRepository.delete(volunteer);
        return recordMapper.toRecord(volunteer);
    }

    /**
     * Метод изменяет параметры волонтера
     *
     * @param id              id волонтера
     * @param volunteerRecord волонтер
     * @return возвращает измененного волонтера
     */
    public VolunteerRecord editVolunteer(Long id, VolunteerRecord volunteerRecord) {
        logger.info("Was invoked method for edit volunteer");
        Volunteer oldVolunteer = volunteerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not volunteer with id = {}", id);
                    return new VolonteerNotFoundException(id);
                });
        oldVolunteer.setName(volunteerRecord.getName());
        oldVolunteer.setLastName(volunteerRecord.getLastName());
        return recordMapper.toRecord(volunteerRepository.save(oldVolunteer));
    }

    /**
     * Метод добавляет волонтеру животное, за которым он следит
     *
     * @param id       id волонтера
     * @param animalId id животного
     * @return возвращает волонтера, который следит за этим животным
     */
    public AnimalRecord patchVolunteerAnimal(Long id, Long animalId) {
        logger.info("Was invoked method for adding animal to volunteer");
        Optional<Volunteer> optionalVolunteer = volunteerRepository.findById(id);
        Optional<Animal> optionalAnimal = animalRepository.findById(animalId);
        if (optionalVolunteer.isEmpty()) {
            logger.error("There is not volunteer with id = {}", id);
            throw new VolonteerNotFoundException(id);
        }
        if (optionalAnimal.isEmpty()) {
            logger.error("There is not animal with id = {}", animalId);
            throw new AnimalNotFoundException(animalId);
        }
        Animal animal = optionalAnimal.get();
        animal.setVolunteer(optionalVolunteer.get());
        return recordMapper.toRecord(animalRepository.save(animal));
    }

    /**
     * Метод находит все отчеты пользователя
     *
     * @param id id пользователя
     * @return возвращает список отчетов
     */
    public Collection<ReportRecord> findReportsByUser(Long id) {
        logger.info("Was invoked method for get reports by user");
        return userRepository.findById(id)
                .map(UserData::getReports)
                .map(reports ->
                        reports.stream()
                                .map(recordMapper::toRecord)
                                .collect(Collectors.toList()))
                .orElseThrow(() -> {
                    logger.error("There is not user with id = {}", id);
                    return new UserNotFoundException(id);
                });
    }

    /**
     * Метод добавляет пользователю животное(Волонтер добавляет животное, когда пользователь забирает его из приюта)
     *
     * @param id       id пользователя
     * @param animalId id животного
     * @return возвращает пользователя, который забрал животное из приюта
     */
    public UserRecord patchUserAnimal(Long id, Long animalId) {
        logger.info("Was invoked method for adding animal to user");
        Optional<UserData> optionalUser = userRepository.findById(id);
        Optional<Animal> optionalAnimal = animalRepository.findById(animalId);
        if (optionalUser.isEmpty()) {
            logger.error("There is not user with id = {}", id);
            throw new UserNotFoundException(id);
        }
        if (optionalAnimal.isEmpty()) {
            logger.error("There is not animal with id = {}", animalId);
            throw new AnimalNotFoundException(animalId);
        }
        UserData userData = optionalUser.get();
        userData.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMonths(1));
        userData.setAnimal(optionalAnimal.get());
        return recordMapper.toRecord(userRepository.save(userData));
    }

    /**
     * Метод ищет пользователя по животному
     *
     * @param animalId - id животного
     * @return возвращает пользователя
     */
    public UserRecord findUserByAnimal(long animalId) {
        logger.info("Was invoked method for get user by animal");
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> {
                    logger.error("There is not animal with id = {}", animalId);
                    return new AnimalNotFoundException(animalId);
                });
        return recordMapper.toRecord(userRepository.findByAnimal(animal));
    }

    /**
     * Метод для продления испытательного срока
     *
     * @param id     id пользователя
     * @param number номер для выбора количества дней продления(14 или 30)
     * @return возвращает пользователя
     */
    public UserRecord extensionPeriod(long id, int number) {
        logger.info("Was invoked method for extension of the probation period");
        UserData user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not user with id = {}", id);
                    return new UserNotFoundException(id);
                });
        if (user.getDate() == null) {
            throw new DateMissException();
        }
        LocalDateTime localDateTime = user.getDate();
        if (number == 1) {
            user.setDate(localDateTime.plusWeeks(2));
            telegramBotUpdatesListener.mailing(user.getChatId(), "Вам продлили период испытательного срока на 14 дней");

        } else if (number == 2) {
            user.setDate(localDateTime.plusMonths(1));
            telegramBotUpdatesListener.mailing(user.getChatId(), "Вам продлили период испытательного срока на 30 дней");
        } else {
            throw new NumberNotFoundException();
        }
        return recordMapper.toRecord(userRepository.save(user));
    }

    /**
     * Метод ищет пользователей по обращению в приют(собак или кошек)
     *
     * @param number номер для выбора вывода информации(приют кошек или приют собак)
     * @return возвращает список пользователей
     */
    public Collection<UserRecord> getAllUserShelterDogOrShelterCat(int number) {
        List<UserRecord> users = new ArrayList<>();
        if (number == 1) {
            return userRepository.findAll().stream()
                    .map(recordMapper::toRecord)
                    .filter(user -> user.getShelter() == 1)
                    .collect(Collectors.toList());
        } else if (number == 2) {
            return userRepository.findAll().stream()
                    .map(recordMapper::toRecord)
                    .filter(user -> user.getShelter() == 2)
                    .collect(Collectors.toList());
        } else {
            throw new NumberNotFoundException();
        }
    }

    /**
     * Метод для отправки сообщений пользователю
     *
     * @param id     id пользователя
     * @param number номер для описания результата выбранных случаев
     * @return возвращает пользователя
     */
    public void sendMessageToUser(Long id, int number) {
        logger.info("Was invoked method for sending messages to user if he passed the probation period or not");
        UserData user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not user with id = {}", id);
                    return new UserNotFoundException(id);
                });
        if (number == 1) {
            telegramBotUpdatesListener.mailing(user.getChatId(), "«Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно, как необходимо. " +
                    "Пожалуйста, подойди ответственнее к этому занятию. " +
                    "В противном случае волонтеры приюта будут обязаны самолично проверять условия содержания животного».");
        } else if (number == 2) {
            telegramBotUpdatesListener.mailing(user.getChatId(), "Вы прошли испытательный срок.");
        } else if (number == 3) {
            telegramBotUpdatesListener.mailing(user.getChatId(), "Вы не прошли испытательный срок.");
        } else {
            throw new Number2NotFoundException();
        }
    }
}


