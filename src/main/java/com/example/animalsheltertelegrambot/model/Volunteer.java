package com.example.animalsheltertelegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

/**
 * Класс животное, для хранения информации о животном
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
    private String volunteerName;
    /**
     * Поле фамилия волонтера
     */
    private String volunteerLastName;

    /**
     * Конструктор - создание нового объекта
     *
     * @param volunteerName      - имя волонтера
     * @param volunteerLastName        - фамилия волонтера
     */
    public Volunteer(String volunteerName, String volunteerLastName) {
        this.volunteerName = volunteerName;
        this.volunteerLastName = volunteerLastName;
    }

    /**
     * Пустой конструктор
     */
    public Volunteer() {

    }
    /**
     * Метод получения значения поля {@link Volunteer#id}
     *
     * @return - возвращает id волонтера
     */
    public long getId() {
        return id;
    }
    /**
     * Метод изменения значения поля {@link Volunteer#id}
     *
     * @param id -id волонтера
     */
    public void setId(long id) {
        this.id = id;
    }
    /**
     * Метод получения значения поля {@link Volunteer#volunteerName}
     *
     * @return - возвращает имя волонтера
     */
    public String getName() {
        return volunteerName;
    }
    /**
     * Метод изменения значения поля {@link Volunteer#volunteerName}
     *
     * @param volunteerName - имя волонтера;
     */
    public void setName(String volunteerName) {
        this.volunteerName = volunteerName;
    }
    /**
     * Метод получения значения поля {@link Volunteer#volunteerLastName}
     *
     * @return - фамилию волонтера
     */
    public String getLastName() {
        return volunteerLastName;
    }
    /**
     * Метод изменения значения поля {@link Volunteer#volunteerLastName}
     *
     * @param volunteerLastName - фамилия волонтера;
     */
    public void setLastName(String volunteerLastName) {
        this.volunteerLastName = volunteerLastName;
    }
    /**
     * Переопределение equals
     *
     * @param o - объект для сравнения
     * @return - возвращает true или false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volunteer volunteer = (Volunteer) o;
        return id == volunteer.id && Objects.equals(volunteerName, volunteer.volunteerName) && Objects.equals(volunteerLastName, volunteer.volunteerLastName);
    }
    /**
     * Переопределение hashCode
     *
     * @return - возвращает переопределенный hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, volunteerName, volunteerLastName);
    }
    /**
     * Переопределение toString
     *
     * @return - возвращает переопределенный toString
     */
    @Override
    public String toString() {
        return "Volunteer{" +
                "id=" + id +
                ", volunteerName='" + volunteerName + '\'' +
                ", volunteerLastName='" + volunteerLastName + '\'' +
                '}';
    }
}
