package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.model.Animal;
import com.example.animalsheltertelegrambot.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AnimalService {
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * Метод находит всех животных в БД
     *
     * @return возвращает список животных
     */
    public Collection<Animal> getAllAnimal() {
        return null;
    }

    /**
     * Метод находит животное по id
     *
     * @param id id животного
     * @return возвращает найденное животное
     */
    public Animal findAnimal(Long id) {
        return null;
    }

    /**
     * Метод создает животное в БД
     *
     * @param animal животное
     * @return возвращает созданное животное
     */
    public Animal createAnimal(Animal animal) {
        return null;
    }

    /**
     * Метод удаляет животное из БД
     *
     * @param id id животного
     * @return возвращает удаленное животное
     */
    public Animal deleteAnimal(Long id) {
        return null;
    }

    /**
     * Метод изменяет параметры животного
     *
     * @param id     id животного
     * @param animal животное
     * @return возвращает измененное животное
     */
    public Animal editAnimal(Long id, Animal animal) {
        return null;
    }
}
