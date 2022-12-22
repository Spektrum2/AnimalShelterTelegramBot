package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.model.Volunteer;
import com.example.animalsheltertelegrambot.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    /**
     * Метод находит всех волонтеров в БД
     *
     * @return возвращает список волонтеров
     */
    public Collection<Volunteer> getAllVolunteer() {
        return null;
    }

    /**
     * Метод находит волонтера по id
     *
     * @param id id волонтера
     * @return возвращает найденного волонтера
     */
    public Volunteer findVolunteer(Long id) {
        return null;
    }

    /**
     * Метод создает волантера в БД
     *
     * @param volunteer волантер
     * @return возвращает созданного волонтера
     */
    public Volunteer createVolunteer(Volunteer volunteer) {
        return null;
    }

    /**
     * Метод удаляет волонтера из БД
     *
     * @param id id волонтера
     * @return возвращает удаленного волонтера
     */
    public Volunteer deleteVolunteer(Long id) {
        return null;
    }

    /**
     * Метод изменяет параметры волонтера
     *
     * @param id        id волонтера
     * @param volunteer волонтер
     * @return возвращает измененного волонтера
     */
    public Volunteer editVolunteer(Long id, Volunteer volunteer) {
        return null;
    }

    /**
     * Метод добавляет волонтеру животное, за которым он следит
     *
     * @param id       id волонтера
     * @param animalId id животного
     * @return возвращает волонтера, который следит за этим животным
     */
    public Volunteer patchVolunteerAnimal(Long id, Long animalId) {
        return null;
    }
}
