package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.listener.TelegramBotUpdatesListener;
import com.example.animalsheltertelegrambot.service.AnimalsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/adminPanel")
public class AnimalsController {
    public AnimalsService animalsService;
    public AnimalsController(AnimalsService animalsService) {
        this.animalsService = animalsService;
    }
    @GetMapping("/allAnimals")
    public List<Animal> getAnimals() {
        return null;
    }
    @PostMapping("/postAnimal")
    public List<Animal> postAnimals() {
        return getAnimals();
    }
    @DeleteMapping("/deleteAnimal")
    public List<Animal> deleteAnimal(@RequestParam("idUser") int a) {
        return getAnimals();
    }
    @PutMapping("/putAnimal")
    public List<Animal> putAnimal(@RequestParam("idUser") int a) {
        return getAnimals();
    }
    @GetMapping("/animal")
    public List<Animal> getAnimal(@RequestParam("idUser") int a) {
        return null;
    }

}
