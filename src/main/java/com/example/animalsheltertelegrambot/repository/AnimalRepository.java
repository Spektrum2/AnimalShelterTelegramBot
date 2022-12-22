package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Репозиторий для хранение информации о животных
 */
public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
