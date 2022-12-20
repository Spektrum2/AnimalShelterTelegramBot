package com.example.animalsheltertelegrambot.model;

import jakarta.persistence.*;

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
     * Конструктор - создание нового объекта
     *
     * @param idChat         - id чата
     * @param name          - login пользователя
     * @param phoneNumber - контактные данные пользователя
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
     * @return - возвращает id пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Метод изменения значения поля {@link UserData#id}
     *
     * @param id -id пользователя
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Метод получения значения поля {@link UserData#idChat}
     *
     * @return - возвращает id чата
     */
    public Long getIdChat() {
        return idChat;
    }

    /**
     * Метод изменения значения поля {@link UserData#idChat}
     *
     * @param idChat - id чата
     */
    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    /**
     * Метод получения значения поля {@link UserData#name}
     *
     * @return - возвращает login пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Метод изменения значения поля {@link UserData#name}
     *
     * @param name - login пользователя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Метод получения значения поля {@link UserData#phoneNumber}
     *
     * @return - возвращает контактные данные пользователя
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Метод изменения значения поля {@link UserData#phoneNumber}
     *
     * @param phoneNumber - контактные данные пользователя
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Переопределение equals
     *
     * @param o - объект для сравнения
     * @return - возвращает true или  false
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
     * @return - возвращает переопределенный hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, idChat, name, phoneNumber);
    }

    /**
     * Переопределение toString
     *
     * @return - возвращает переопределенный toString
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
