package com.example.animalsheltertelegrambot.repository;

import com.example.animalsheltertelegrambot.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий
 */
public interface ReportRepository extends JpaRepository<Report,Long> {

}
