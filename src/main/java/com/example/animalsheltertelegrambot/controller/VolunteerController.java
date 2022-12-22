package com.example.animalsheltertelegrambot.controller;

import com.example.animalsheltertelegrambot.listener.TelegramBotUpdatesListener;
import com.example.animalsheltertelegrambot.service.VolunteerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/adminPanel")
public class VolunteerController {
    VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping("/allVolunteer")
    public List<Volunteer> getVolunteers() {
        return null;
    }

    @PostMapping("/postVolunteer")
    public List<Volunteer> postVolunteer() {
        return getVolunteer();
    }

    @DeleteMapping("/deleteVolunteer")
    public List<Volunteer> deleteVolunteer(@RequestParam("idUser") int a) {
        return getVolunteer();
    }

    @PutMapping("/putVolunteer")
    public List<Volunteer> putVolunteer(@RequestParam("idUser") int a) {
        return getVolunteer();
    }

    @GetMapping("/Volunteer")
    public List<Volunteer> getVolunteer(@RequestParam("idUser") int a) {
        return null;
    }
}
