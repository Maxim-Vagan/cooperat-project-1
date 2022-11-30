package ru.jd6team7.cooperatproject1.model.visitor;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/*
Сущность "Посетитель" хранит информацию о посетителях Приюта.
ФИО, Контактные данные для связи, Признак того, что Посетитель Новенький
*/
@Entity
@Getter
@Setter
@Table(name = "dog_visitor")
public class DogVisitor{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    @Column(name = "chat_id")
    private Long chatId;

    public DogVisitor(Long id, String name, String phoneNumber, String email, Long chatId) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.chatId = chatId;
    }
    public DogVisitor(long chatId) {
        this.chatId = chatId;
    }
    public DogVisitor() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DogVisitor dogVisitor = (DogVisitor) o;
        return name.equals(dogVisitor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
