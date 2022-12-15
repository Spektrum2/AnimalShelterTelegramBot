package com.example.animalsheltertelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnimalShelterTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnimalShelterTelegramBotApplication.class, args);
    }

}
