package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.listener.TelegramBotUpdatesListener;
import com.example.animalsheltertelegrambot.model.UserData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adminPanel")
public class AdminPanelController {
    public TelegramBotUpdatesListener telegramBotUpdatesListener;

    public AdminPanelController(TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }

    @GetMapping("/allUsers")
    public List<UserData> getUser() {
        return null;
    }

    @PostMapping("/postUsers")
    public List<UserData> postUser() { // передеать параметры для занесения пользователя в БД через @RequestParam
        return getUser();
    }

    @DeleteMapping("/deleteUser")
    public List<UserData> deleteUser(@RequestParam("idUser") int a) {
        return getUser();
    }

    @PutMapping("/putUser")
    public List<UserData> putUser(@RequestParam("idUser") int a) {
        return getUser();
    }
}
