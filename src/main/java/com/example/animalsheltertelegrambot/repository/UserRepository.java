package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий
 */
public interface UserRepository extends JpaRepository<UserData, Long> {

}
