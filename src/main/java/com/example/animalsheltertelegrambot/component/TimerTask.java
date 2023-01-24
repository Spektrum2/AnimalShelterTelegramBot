package com.example.animalsheltertelegrambot.component;

import com.example.animalsheltertelegrambot.model.Report;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.repository.UserRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Component
public class TimerTask {
    private final UserRepository userRepository;
    private final TelegramBot telegramBot;

    public TimerTask(UserRepository userRepository, TelegramBot telegramBot) {
        this.userRepository = userRepository;
        this.telegramBot = telegramBot;
    }

    /**
     * Метод для атоматического послания сообщений
     */
    @Scheduled(cron = "0 21 * * * *")
    @Transactional(readOnly = true)
    public void warning() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        userRepository.findAll().stream()
                .map(UserData::getReports)
                .map(reports ->
                        reports.stream()
                                .reduce((first, second) -> second)
                                .orElse(null))
                .filter(Objects::nonNull)
                .filter(report -> ChronoUnit.DAYS.between(report.getDate(), now) == 1 || ChronoUnit.DAYS.between(report.getDate(), now) == 2)
                .map(Report::getUserData)
                .forEach(user -> telegramBot.execute(new SendMessage(user.getChatId(), "Сделайте отчет")));


        userRepository.findAll().stream()
                .map(UserData::getReports)
                .map(reports ->
                        reports.stream()
                                .reduce((first, second) -> second)
                                .orElse(null))
                .filter(Objects::nonNull)
                .filter(report -> ChronoUnit.DAYS.between(report.getDate(), now) > 2)
                .map(Report::getUserData)
                .map(UserData::getAnimal)
                .forEach(animal -> telegramBot.execute(new SendMessage(animal.getVolunteer().getChatId(), "Пользователь с животным id - " + animal.getId() + " не делает отчеты больше 2-х дней")));

        userRepository.findAll().stream()
                .filter(user -> user != null && user.getDate().toLocalDate().equals(now.toLocalDate()))
                .map(UserData::getAnimal)
                .filter(animal -> animal != null && animal.getVolunteer() != null)
                .forEach(animal -> telegramBot.execute(new SendMessage(animal.getVolunteer().getChatId(), "У пользователя с животным id - " + animal.getId() + " закончился испытательный период")));
    }
}
