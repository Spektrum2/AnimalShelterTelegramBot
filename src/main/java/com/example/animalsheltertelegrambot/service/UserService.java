package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.model.Report;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Метод находит всех пользователей в БД
     *
     * @return возвращает список пользователей
     */
    public Collection<UserData> getAllUsers() {
        return null;
    }

    /**
     * Метод находит пользователя по id
     *
     * @param id id пользователя
     * @return возвращает найденного пользователя
     */
    public UserData findUser(Long id) {
        return null;
    }

    /**
     * Метод создает пользователя в БД
     *
     * @param userData пользователь
     * @return возвращает созданного пользователя
     */
    public UserData createUser(UserData userData) {
        return null;
    }

    /**
     * Метод удаляет пользователя из БД
     *
     * @param id id пользователя
     * @return возвращает удаленного пользователя
     */
    public UserData deleteUser(Long id) {
        return null;
    }

    /**
     * Метод изменяет параметры пользователя
     *
     * @param id       id пользователя
     * @param userData пользователь
     * @return возвращает измененного пользователя
     */
    public UserData editUser(Long id, UserData userData) {
        return null;
    }

    /**
     * Метод находит все отчеты пользователя
     *
     * @param id id пользователя
     * @return возвращает список отчетов
     */
    public Collection<Report> findReportsByUser(Long id) {
        return null;
    }

    /**
     * Метод добавляет пользователю животное(Волонтер добавляет животное, когда пользователь забирает его из приюта)
     *
     * @param id       id пользователя
     * @param animalId id животного
     * @return возвращает пользователя, который забрал животное из приюта
     */
    public UserData patchUserAnimal(Long id, Long animalId) {
        return null;
    }
}
