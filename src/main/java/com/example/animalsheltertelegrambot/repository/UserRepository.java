package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.Animal;
import com.example.animalsheltertelegrambot.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для пользователей
 */
public interface UserRepository extends JpaRepository<UserData, Long> {
    UserData findByAnimal(Animal animal);

    List<UserData> findByDate(LocalDateTime localDateTime);
}
