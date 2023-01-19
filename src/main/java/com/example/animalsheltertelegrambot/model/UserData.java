package com.example.animalsheltertelegrambot.model;

import javax.persistence.*;

import java.time.LocalDateTime;
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
    private Long chatId;
    /**
     * Поле login пользователя
     */
    private String name;
    /**
     * Поле для хранения контактных данных пользователя
     */
    private String phoneNumber;

    /**
     * Поле идентификатор приюта
     */
    private Integer shelter;

    /**
     * Поле для даты оканчания испытательного периода
     */
    private LocalDateTime date;

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
     * @param chatId     id чата
     * @param name        login пользователя
     * @param phoneNumber контактные данные пользователя
     */
    public UserData(Long chatId, String name, String phoneNumber, Integer shelter) {
        this.chatId = chatId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.shelter = shelter;
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
     * Метод получения значения поля {@link UserData#chatId}
     *
     * @return возвращает id чата
     */
    public Long getChatId() {
        return chatId;
    }

    /**
     * Метод изменения значения поля {@link UserData#chatId}
     *
     * @param chatId id чата
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId;
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

    public Integer getShelter() {
        return shelter;
    }

    public void setShelter(Integer shelter) {
        this.shelter = shelter;
    }
    /**
     * Метод получения значения поля {@link UserData#date}
     *
     * @return возврощает дату оканчания испытательного периода
     */

    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Метод изменения значения поля {@link UserData#date}
     *
     * @param date дата оканчания испытательного периода
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
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
        return Objects.equals(id, userData.id) && Objects.equals(chatId, userData.chatId) && Objects.equals(name, userData.name) && Objects.equals(phoneNumber, userData.phoneNumber);
    }

    /**
     * Переопределение hashCode
     *
     * @return возвращает переопределенный hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, name, phoneNumber);
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
                ", idChat=" + chatId +
                ", login='" + name + '\'' +
                ", contactDetails='" + phoneNumber + '\'' +
                '}';
    }
}
