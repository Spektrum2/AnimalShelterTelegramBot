package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.PhotoOfAnimal;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для фотографий животных
 */
public interface PhotoRepository extends JpaRepository<PhotoOfAnimal, Long> {
}
