package ru.jd6team7.cooperatproject1.model.visitor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Objects;

//Юзер, обратившийся в чат. Содержит инфу о стадии общения с ботом и инфу о чате. Связан с DogVisitor и CatVisitor
@Entity
@Getter
@Setter
public class Visitor {



    public enum MessageStatus {BASE, SHELTER_INFO, PET_INFO, GET_CALLBACK, SEND_DAILY_REPORT;}

    public enum ShelterStatus {DOG, CAT;}
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    @NonNull
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    @Column(name = "need_callback")
    private boolean needCallback;
    @Enumerated(EnumType.STRING)
    @Column(name = "message_status")
    private MessageStatus messageStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "shelter_status")
    private ShelterStatus shelterStatus;

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
        return chatId.equals(visitor.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }

    @Override
    public String toString() {
        return "Visitor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", chatId=" + chatId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", needCallback=" + needCallback +
                ", messageStatus=" + messageStatus +
                ", shelterStatus=" + shelterStatus +
                '}';
    }
}
