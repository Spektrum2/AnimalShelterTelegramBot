package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
