package ru.jd6team7.cooperatproject1.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Objects;

/*
Сущность "Посетитель" хранит информацию о посетителях Приюта.
ФИО, Контактные данные для связи, Признак того, что Посетитель Новенький
*/
@Entity
@Getter
@Setter
public class Visitor {

    public enum MessageStatus {BASE, SHELTER_INFO, GET_PET_INFO}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(name = "phone_number")
    String phoneNumber;
    String email;
    @NonNull
    @Column(name = "chat_id")
    long chatId;
    @Column(name = "status")
    MessageStatus messageStatus;

    public Visitor(Long id, @NonNull String name, @NonNull String phoneNumber, String email, long chatId, MessageStatus messageStatus) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.chatId = chatId;
        this.messageStatus = messageStatus;
    }

    public Visitor(long chatId) {
        this.chatId = chatId;
    }

    public Visitor() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visitor visitor = (Visitor) o;
        return name.equals(visitor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
