package com.example.animalsheltertelegrambot.model;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Класс пользователь для хранения контактных данных пользователя
 */
@Entity
public class User {
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
    private String login;
    /**
     * Поле для хранения контактных данных пользователя
     */
    private String contactDetails;

    /**
     * Конструктор - создание нового объекта
     *
     * @param idChat         - id чата
     * @param login          - login пользователя
     * @param contactDetails - контактные данные пользователя
     */
    public User(Long idChat, String login, String contactDetails) {
        this.idChat = idChat;
        this.login = login;
        this.contactDetails = contactDetails;
    }

    /**
     * Пустой конструктор
     */
    public User() {
    }

    /**
     * Метод получения значения поля {@link User#id}
     *
     * @return - возвращает id пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Метод изменения значения поля {@link User#id}
     *
     * @param id -id пользователя
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Метод получения значения поля {@link User#idChat}
     *
     * @return - возвращает id чата
     */
    public Long getIdChat() {
        return idChat;
    }

    /**
     * Метод изменения значения поля {@link User#idChat}
     *
     * @param idChat - id чата
     */
    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    /**
     * Метод получения значения поля {@link User#login}
     *
     * @return - возвращает login пользователя
     */
    public String getLogin() {
        return login;
    }

    /**
     * Метод изменения значения поля {@link User#login}
     *
     * @param login - login пользователя
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Метод получения значения поля {@link User#contactDetails}
     *
     * @return - возвращает контактные данные пользователя
     */
    public String getContactDetails() {
        return contactDetails;
    }

    /**
     * Метод изменения значения поля {@link User#contactDetails}
     *
     * @param contactDetails - контактные данные пользователя
     */
    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
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
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(idChat, user.idChat) && Objects.equals(login, user.login) && Objects.equals(contactDetails, user.contactDetails);
    }

    /**
     * Переопределение hashCode
     *
     * @return - возвращает переопределенный hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, idChat, login, contactDetails);
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
                ", login='" + login + '\'' +
                ", contactDetails='" + contactDetails + '\'' +
                '}';
    }
}
