package com.example.animalsheltertelegrambot.model;

import javax.persistence.*;

import java.util.List;
import java.util.Objects;

/**
 * Класс Волонтер, для хранения информации о волонтере
 */
@Entity
public class Volunteer {
    /**
     * Поле id животного
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Поле имя волонтера
     */
    private String name;
    /**
     * Поле фамилия волонтера
     */
    private String lastName;
    @OneToMany(mappedBy = "volunteer")
    private List<Animal> animals;

    /**
     * Конструктор создание нового объекта
     *
     * @param volunteerName     имя волонтера
     * @param volunteerLastName фамилия волонтера
     */
    public Volunteer(String volunteerName, String volunteerLastName) {
        this.name = volunteerName;
        this.lastName = volunteerLastName;
    }

    /**
     * Пустой конструктор
     */
    public Volunteer() {

    }

    /**
     * Метод получения значения поля {@link Volunteer#id}
     *
     * @return возвращает id волонтера
     */
    public long getId() {
        return id;
    }

    /**
     * Метод изменения значения поля {@link Volunteer#id}
     *
     * @param id id волонтера
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Метод получения значения поля {@link Volunteer#name}
     *
     * @return возвращает имя волонтера
     */
    public String getName() {
        return name;
    }

    /**
     * Метод изменения значения поля {@link Volunteer#name}
     *
     * @param volunteerName имя волонтера;
     */
    public void setName(String volunteerName) {
        this.name = volunteerName;
    }

    /**
     * Метод получения значения поля {@link Volunteer#lastName}
     *
     * @return фамилию волонтера
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Метод изменения значения поля {@link Volunteer#lastName}
     *
     * @param volunteerLastName - фамилия волонтера;
     */
    public void setLastName(String volunteerLastName) {
        this.lastName = volunteerLastName;
    }

    /**
     * Метод получения значения поля {@link Volunteer#animals}
     *
     * @return возвращает животное
     */
    public List<Animal> getAnimals() {
        return animals;
    }

    /**
     * Метод изменения значения поля {@link Volunteer#animals}
     *
     * @param animals животное
     */
    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
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
        Volunteer volunteer = (Volunteer) o;
        return id == volunteer.id && Objects.equals(name, volunteer.name) && Objects.equals(lastName, volunteer.lastName);
    }

    /**
     * Переопределение hashCode
     *
     * @return возвращает переопределенный hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName);
    }

    /**
     * Переопределение toString
     *
     * @return возвращает переопределенный toString
     */
    @Override
    public String toString() {
        return "Volunteer{" +
                "id=" + id +
                ", volunteerName='" + name + '\'' +
                ", volunteerLastName='" + lastName + '\'' +
                '}';
    }
}
