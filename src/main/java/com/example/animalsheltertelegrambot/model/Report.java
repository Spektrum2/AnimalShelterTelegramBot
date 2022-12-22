package com.example.animalsheltertelegrambot.model;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Класс отчет для хранения информации о том, как животное чувствует себя на новом месте
 */
@Entity
public class Report {
    /**
     * Поле id отчета
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Поле для хранения имени животного
     */
    private String name;
    /**
     * Поле для хранения информации о рационе животного
     */
    private String diet;
    /**
     * Поле для хранения информации о состоянии здоровья животного
     */
    private String health;
    /**
     * Поле,которое описывает изменения в поведении у животного
     */
    private String behaviorChange;
    /**
     * Поле для связи с таблицей Userdata
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;
    /**
     * Поле для связи с таблицей PhotoOfAnimal
     */
    @OneToOne
    private PhotoOfAnimal photoOfAnimal;

    /**
     * Конструктор - создание нового объекта
     *
     * @param name           имя животного
     * @param diet           рацион животного
     * @param health         состояние здоровья
     * @param behaviorChange изменения в поведении
     */
    public Report(String name, String diet, String health, String behaviorChange) {
        this.name = name;
        this.diet = diet;
        this.health = health;
        this.behaviorChange = behaviorChange;
    }

    /**
     * Пустой констуктор
     */
    public Report() {

    }

    /**
     * Метод получения значения поля {@link Report#id}
     *
     * @return возвращает id отчета
     */
    public Long getId() {
        return id;
    }

    /**
     * Метод  изменения значения поля {@link Report#id}
     *
     * @return возвращает id отчета
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Метод получения значения имени животного {@link Report#name}
     *
     * @return возвращает имя животного
     */
    public String getName() {
        return name;
    }


    /**
     * Метод изменения значения имени животного {@link Report#name}
     *
     * @param name имя животного
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Метод получения информации о рационе животного {@link Report#diet}
     *
     * @return возвращает рацион
     */
    public String getDiet() {
        return diet;
    }


    /**
     * Метод изменения значения  поля рациона {@link Report#diet}
     *
     * @param diet рацион животного
     */
    public void setDiet(String diet) {
        this.diet = diet;
    }

    /**
     * Метод получения информации о состоянии здоровья животного {@link Report#health}
     *
     * @return возвращает информацию о состоянии здоровья
     */
    public String getHealth() {
        return health;
    }


    /**
     * Метод изменения информации о состоянии здоровья животного {@link Report#health}
     *
     * @param health состояние здоровья животного
     */
    public void setHealth(String health) {
        this.health = health;
    }

    /**
     * Метод получения информации о изменении поведения животного {@link Report#behaviorChange}
     *
     * @return возвращает информацию об изменении поведения животного
     */
    public String getBehaviorChange() {
        return behaviorChange;
    }


    /**
     * Метод изменения информации о изменении поведения животного {@link Report#behaviorChange}
     *
     * @param behaviorChange изменение в поведении
     */
    public void setBehaviorChange(String behaviorChange) {
        this.behaviorChange = behaviorChange;
    }

    /**
     * Метод получения значения поля {@link Report#userData}
     *
     * @return возвращает пользователя
     */
    public UserData getUserData() {
        return userData;
    }

    /**
     * Метод изменения значения поля {@link Report#userData}
     *
     * @param userData пользователь
     */
    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    /**
     * Метод получения значения поля {@link Report#photoOfAnimal}
     *
     * @return возвращает фотографию животного
     */
    public PhotoOfAnimal getPhotoOfAnimal() {
        return photoOfAnimal;
    }

    /**
     * Метод изменения значения поля {@link Report#photoOfAnimal}
     *
     * @param photoOfAnimal фотография животного
     */
    public void setPhotoOfAnimal(PhotoOfAnimal photoOfAnimal) {
        this.photoOfAnimal = photoOfAnimal;
    }

    /**
     * Переопределение equals
     *
     * @param o объект для сравнения
     * @return возвращает true или  false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) && Objects.equals(name, report.name) && Objects.equals(diet, report.diet) && Objects.equals(health, report.health) && Objects.equals(behaviorChange, report.behaviorChange);
    }

    /**
     * Переопределение hashCode
     *
     * @return возвращает переопределенный hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, diet, health, behaviorChange);
    }

    /**
     * Переопределение toString
     *
     * @return возвращает переопределенный toString
     */
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", diet='" + diet + '\'' +
                ", health='" + health + '\'' +
                ", behaviorChange='" + behaviorChange + '\'' +
                '}';
    }
}
