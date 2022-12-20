package com.example.animalsheltertelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main-class
 */
@SpringBootApplication
@EnableScheduling
public class AnimalShelterTelegramBotApplication {

    /**
     * Метод стартующий Spring
     * @param args - аргументы командной строки (запуска)
     */
    public static void main(String[] args) {
        SpringApplication.run(AnimalShelterTelegramBotApplication.class, args);
    }

}
