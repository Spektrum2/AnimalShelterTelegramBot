package com.example.animalsheltertelegrambot.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

/**
 * Класс пользователь для хранения контактных данных пользователя
 */
@Entity
public class UserData {
    /**
     * Поле id пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Поле id чата
     */
    private Long idChat;
    /**
     * Поле login пользователя
     */
    private String name;
    /**
     * Поле для хранения контактных данных пользователя
     */
    private String phoneNumber;

    /**
     * Поле для связи с таблицей Report
     */
    @OneToMany(mappedBy = "userData")
    private List<Report> reports;

    /**
     * Поле для связи с таблицей Animal
     */
    @OneToOne
    private Animal animal;

    /**
     * Конструктор - создание нового объекта
     *
     * @param idChat      id чата
     * @param name        login пользователя
     * @param phoneNumber контактные данные пользователя
     */
    public UserData(Long idChat, String name, String phoneNumber) {
        this.idChat = idChat;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Пустой конструктор
     */
    public UserData() {
    }

    /**
     * Метод получения значения поля {@link UserData#id}
     *
     * @return возвращает id пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Метод изменения значения поля {@link UserData#id}
     *
     * @param id id пользователя
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Метод получения значения поля {@link UserData#idChat}
     *
     * @return возвращает id чата
     */
    public Long getIdChat() {
        return idChat;
    }

    /**
     * Метод изменения значения поля {@link UserData#idChat}
     *
     * @param idChat id чата
     */
    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    /**
     * Метод получения значения поля {@link UserData#name}
     *
     * @return возвращает имя пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Метод изменения значения поля {@link UserData#name}
     *
     * @param name имя пользователя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Метод получения значения поля {@link UserData#phoneNumber}
     *
     * @return возвращает телефон пользователя
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Метод изменения значения поля {@link UserData#phoneNumber}
     *
     * @param phoneNumber телефон пользователя
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Метод получения значения поля {@link UserData#reports}
     *
     * @return возвращает все отчеты пользователя
     */
    public List<Report> getReports() {
        return reports;
    }

    /**
     * Метод изменения значения поля {@link UserData#reports}
     *
     * @param reports отчеты пользователя
     */
    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    /**
     * Метод получения значения поля {@link UserData#animal}
     *
     * @return возврощает животное
     */
    public Animal getAnimal() {
        return animal;
    }

    /**
     * Метод изменения значения поля {@link UserData#animal}
     *
     * @param animal животное
     */
    public void setAnimal(Animal animal) {
        this.animal = animal;
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
        UserData userData = (UserData) o;
        return Objects.equals(id, userData.id) && Objects.equals(idChat, userData.idChat) && Objects.equals(name, userData.name) && Objects.equals(phoneNumber, userData.phoneNumber);
    }

    /**
     * Переопределение hashCode
     *
     * @return возвращает переопределенный hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, idChat, name, phoneNumber);
    }

    /**
     * Переопределение toString
     *
     * @return возвращает переопределенный toString
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", idChat=" + idChat +
                ", login='" + name + '\'' +
                ", contactDetails='" + phoneNumber + '\'' +
                '}';
    }
}
