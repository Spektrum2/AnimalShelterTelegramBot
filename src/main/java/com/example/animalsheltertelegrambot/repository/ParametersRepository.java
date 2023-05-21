package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.Parameters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametersRepository  extends JpaRepository<Parameters, Long> {
}
