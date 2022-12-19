package com.example.animalsheltertelegrambot.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idChat;
    private String login;
    private String contactDetails;

    public User(Long idChat, String login, String contactDetails) {
        this.idChat = idChat;
        this.login = login;
        this.contactDetails = contactDetails;
    }
    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(idChat, user.idChat) && Objects.equals(login, user.login) && Objects.equals(contactDetails, user.contactDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idChat, login, contactDetails);
    }

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
