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

    public enum MessageStatus {BASE, SHELTER_INFO, GET_PET_INFO, GET_CALLBACK}
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    @NonNull
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;
    @Column(name = "need_callback")
    private boolean needCallback;

    public Visitor(Integer id, @NonNull String name, @NonNull String phoneNumber, String email, Long chatId, MessageStatus messageStatus) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.chatId = chatId;
        this.messageStatus = messageStatus;
    }
    public Visitor(Long chatId) {
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
