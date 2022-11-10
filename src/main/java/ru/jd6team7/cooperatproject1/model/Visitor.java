package ru.jd6team7.cooperatproject1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/*
Сущность "Посетитель" хранит информацию о посетителях Приюта.
ФИО, Контактные данные для связи, Признак того, что Посетитель Новенький
*/
@Entity
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String visitorName;
    String visitorSurname;
    String visitorLastname;
    String phoneNumber;
    String email;
    Boolean isNewTelegramVisitor;
    String telegramChatAlias;

    public Long getId() {
        return id;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorSurname() {
        return visitorSurname;
    }

    public void setVisitorSurname(String visitorSurname) {
        this.visitorSurname = visitorSurname;
    }

    public String getVisitorLastname() {
        return visitorLastname;
    }

    public void setVisitorLastname(String visitorLastname) {
        this.visitorLastname = visitorLastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getNewTelegramVisitor() {
        return isNewTelegramVisitor;
    }

    public void setNewTelegramVisitor(Boolean newTelegramVisitor) {
        isNewTelegramVisitor = newTelegramVisitor;
    }

    public String getTelegramChatAlias() {
        return telegramChatAlias;
    }

    public void setTelegramChatAlias(String telegramChatAlias) {
        this.telegramChatAlias = telegramChatAlias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visitor)) return false;
        Visitor visitor = (Visitor) o;
        return Objects.equals(getVisitorName(), visitor.getVisitorName()) && Objects.equals(getVisitorSurname(), visitor.getVisitorSurname()) && Objects.equals(getVisitorLastname(), visitor.getVisitorLastname()) && Objects.equals(getPhoneNumber(), visitor.getPhoneNumber()) && Objects.equals(getEmail(), visitor.getEmail()) && Objects.equals(getTelegramChatAlias(), visitor.getTelegramChatAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVisitorName(), getVisitorSurname(), getVisitorLastname(), getPhoneNumber(), getEmail(), getTelegramChatAlias());
    }
}
