package com.example.animalsheltertelegrambot.record;

import com.example.animalsheltertelegrambot.model.AnimalType;

import javax.validation.constraints.NotBlank;

public class AnimalRecord {
    private long id;
    private AnimalType animalType;
    @NotBlank(message = "Имя животного должно быть заполнено!")
    private String animalName;
    private VolunteerRecord volunteer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    public void setAnimalType(AnimalType animalType) {
        this.animalType = animalType;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public VolunteerRecord getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(VolunteerRecord volunteer) {
        this.volunteer = volunteer;
    }
}
