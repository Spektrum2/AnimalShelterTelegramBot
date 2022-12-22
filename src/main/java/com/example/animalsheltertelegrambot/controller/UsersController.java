package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.listener.TelegramBotUpdatesListener;
import com.example.animalsheltertelegrambot.model.UserData;
import com.example.animalsheltertelegrambot.service.UsersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adminPanel")
public class UsersController {
    public UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/allUsers")
    public List<UserData> getUsers() {
        return null;
    }

    @PostMapping("/postUsers")
    public List<UserData> postUser() { // передеать параметры для занесения пользователя в БД через @RequestParam
        return getUsers();
    }

    @DeleteMapping("/deleteUser")
    public List<UserData> deleteUser(@RequestParam("idUser") int a) {
        return getUsers();
    }

    @PutMapping("/putUser")
    public List<UserData> putUser(@RequestParam("idUser") int a) {
        return getUsers();
    }

    @GetMapping("/user")
    public List<UserData> getUser(@RequestParam("idUser") int a) {
        return null;
    }

}
