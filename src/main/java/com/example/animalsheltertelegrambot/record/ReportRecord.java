package com.example.animalsheltertelegrambot.record;

import java.time.LocalDateTime;

public class ReportRecord {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String diet;
    private String health;
    private String behaviorChange;
    private UserRecord user;
    private PhotoOfAnimalRecord photoOfAnimal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getBehaviorChange() {
        return behaviorChange;
    }

    public void setBehaviorChange(String behaviorChange) {
        this.behaviorChange = behaviorChange;
    }

    public UserRecord getUser() {
        return user;
    }

    public void setUser(UserRecord user) {
        this.user = user;
    }

    public PhotoOfAnimalRecord getPhotoOfAnimal() {
        return photoOfAnimal;
    }

    public void setPhotoOfAnimal(PhotoOfAnimalRecord photoOfAnimal) {
        this.photoOfAnimal = photoOfAnimal;
    }
}
