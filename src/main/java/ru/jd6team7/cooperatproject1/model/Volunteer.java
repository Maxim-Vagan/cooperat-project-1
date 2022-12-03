package ru.jd6team7.cooperatproject1.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/*
Сущность Волонтер хранит необходимую информацию о волонтёрах:
ФИО, ID чата для уведомлений бот-ассистента волонтёром
*/
@Entity
@Getter
@Setter
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;
    String surname;
    String lastname;
    @Column(name = "phone_number")
    String phoneNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Volunteer)) return false;
        Volunteer volunteer = (Volunteer) o;
        return Objects.equals(getName(), volunteer.getName()) && Objects.equals(getSurname(), volunteer.getSurname()) && Objects.equals(getLastname(), volunteer.getLastname()) && Objects.equals(getPhoneNumber(), volunteer.getPhoneNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSurname(), getLastname(), getPhoneNumber());
    }
}
