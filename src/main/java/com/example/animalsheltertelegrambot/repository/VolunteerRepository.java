package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.Animal;
import com.example.animalsheltertelegrambot.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
}
