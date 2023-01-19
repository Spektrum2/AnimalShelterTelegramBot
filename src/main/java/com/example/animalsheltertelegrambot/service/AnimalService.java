package com.example.animalsheltertelegrambot.service;

import com.example.animalsheltertelegrambot.component.RecordMapper;
import com.example.animalsheltertelegrambot.exception.AnimalNotFoundException;
import com.example.animalsheltertelegrambot.model.Animal;
import com.example.animalsheltertelegrambot.record.AnimalRecord;
import com.example.animalsheltertelegrambot.record.VolunteerRecord;
import com.example.animalsheltertelegrambot.repository.AnimalRepository;
import com.example.animalsheltertelegrambot.repository.VolunteerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnimalService {
    private final Logger logger = LoggerFactory.getLogger(AnimalService.class);
    private final AnimalRepository animalRepository;
    private final VolunteerRepository volunteerRepository;
    private final RecordMapper recordMapper;

    public AnimalService(AnimalRepository animalRepository,
                         VolunteerRepository volunteerRepository,
                         RecordMapper recordMapper) {
        this.animalRepository = animalRepository;
        this.volunteerRepository = volunteerRepository;
        this.recordMapper = recordMapper;
    }

    /**
     * Метод находит всех животных в БД
     *
     * @return возвращает список животных
     */
    public Collection<AnimalRecord> getAllAnimal() {
        logger.info("Was invoked method for get all animals");
        return animalRepository.findAll().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }
    /**
     * Метод находит животное по id
     *
     * @param id id животного
     * @return возвращает найденное животное
     */
    public AnimalRecord findAnimal(Long id) {
        logger.info("Was invoked method  for find animal");
        return recordMapper.toRecord(animalRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not animal with id = {}", id);
                    return new AnimalNotFoundException(id);
                }));
    }

    /**
     * Метод создает животное в БД
     *
     * @param animalRecord животное
     * @return возвращает созданное животное
     */
    public AnimalRecord createAnimal(AnimalRecord animalRecord) {
        logger.info("Was invoked method for create animal");
        Animal animal = recordMapper.toEntity(animalRecord);
        animal.setVolunteer(
                Optional.ofNullable(animalRecord.getVolunteer())
                        .map(VolunteerRecord::getId)
                        .flatMap(volunteerRepository::findById)
                        .orElse(null)
        );
        return recordMapper.toRecord(animalRepository.save(animal));
    }

    /**
     * Метод удаляет животное из БД
     *
     * @param id id животного
     * @return возвращает удаленное животное
     */
    public Animal deleteAnimal(Long id) {
        logger.info("Was invoked method for delete animal");
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not animal with id = {}", id);
                    return new AnimalNotFoundException(id);
                });
        animalRepository.delete(animal);
        return animal;
    }

    /**
     * Метод изменяет параметры животного
     *
     * @param id     id животного
     * @param animalRecord животное
     * @return возвращает измененное животное
     */
    public AnimalRecord editAnimal(Long id, AnimalRecord animalRecord) {
        logger.info("Was invoked method for edit animal");
        Animal oldAnimal = animalRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not animal with id = {}", id);
                    return new AnimalNotFoundException(id);
                });
        oldAnimal.setAnimalName(animalRecord.getAnimalName());
        oldAnimal.setAnimalType(animalRecord.getAnimalType());
        oldAnimal.setVolunteer(
                Optional.ofNullable(animalRecord.getVolunteer())
                        .map(VolunteerRecord::getId)
                        .flatMap(volunteerRepository::findById)
                        .orElse(null)
        );
        return recordMapper.toRecord(animalRepository.save(oldAnimal));
    }
}
