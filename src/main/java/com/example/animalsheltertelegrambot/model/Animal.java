package com.example.animalsheltertelegrambot.model;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Класс животное, для хранения информации о животном
 */
@Entity
public class Animal {
    /**
     * Поле id животного
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Поле тип животного(кошка,собака)
     */
    @Enumerated(EnumType.STRING)
    private AnimalType animalType;
    /**
     * Поле имя животного
     */
    private String animalName;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

    /**
     * Конструктор  создание нового объекта
     *
     * @param animalType тип животного
     * @param animalName имя животного
     */

    public Animal(AnimalType animalType, String animalName) {
        this.animalType = animalType;
        this.animalName = animalName;
    }

    /**
     * Пустой конструктор
     */

    public Animal() {

    }

    /**
     * Метод получения значения поля {@link Animal#id}
     *
     * @return возвращает id животного
     */
    public long getId() {
        return id;
    }

    /**
     * Метод изменения значения поля {@link Animal#id}
     *
     * @param id -id животного
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Метод получения значения поля {@link Animal#animalType}
     *
     * @return возвращает тип животного
     */
    public AnimalType getAnimalType() {
        return animalType;
    }

    /**
     * Метод изменения значения поля {@link Animal#animalType}
     *
     * @param animalType тип животного;
     */
    public void setAnimalType(AnimalType animalType) {
        this.animalType = animalType;
    }

    /**
     * Метод получения значения поля {@link Animal#animalName}
     *
     * @return возвращает имя животного
     */
    public String getAnimalName() {
        return animalName;
    }

    /**
     * Метод изменения значения поля {@link Animal#animalName}
     *
     * @param animalName имя животного;
     */
    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    /**
     * Метод получения значения поля {@link Animal#volunteer}
     *
     * @return возвращает волонтера
     */
    public Volunteer getVolunteer() {
        return volunteer;
    }

    /**
     * Метод изменения значения поля {@link Animal#volunteer}
     *
     * @param volunteer волонтер
     */
    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    /**
     * Переопределение equals
     *
     * @param o объект для сравнения
     * @return возвращает true или false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id == animal.id && Objects.equals(animalType, animal.animalType) && Objects.equals(animalName, animal.animalName);
    }

    /**
     * Переопределение hashCode
     *
     * @return возвращает переопределенный hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, animalType, animalName);
    }

    /**
     * Переопределение toString
     *
     * @return возвращает переопределенный toString
     */
    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", animalType='" + animalType + '\'' +
                ", animalName='" + animalName + '\'' +
                '}';
    }
}
