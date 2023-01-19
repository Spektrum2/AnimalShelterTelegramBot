package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterRepository  extends JpaRepository<Shelter, Long> {
}
