package ru.jd6team7.cooperatproject1.model.visitor;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/*
Юзер, обратившийся за питомцем. Данные вносят вручную, связан с Visitor, в котором инфа о чате
*/
@Entity
@Getter
@Setter
@Table(name = "cat_visitor")
public class CatVisitor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    @Column(name = "chat_id")
    private long chatId;

    public CatVisitor(Long id, String name, String phoneNumber, String email, long chatId) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.chatId = chatId;
    }
    public CatVisitor(long chatId) {
        this.chatId = chatId;
    }
    public CatVisitor() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatVisitor catVisitor = (CatVisitor) o;
        return name.equals(catVisitor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
