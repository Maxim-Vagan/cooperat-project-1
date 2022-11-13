package ru.jd6team7.cooperatproject1.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/*
Сущность "Приют" хранит необходимую информацию об организации:
Текст описания деятельности, График работы, Справочную инфу.
*/
@Entity
@Getter
@Setter
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(name = "name_of_director")
    String directorFIO;
    String address;

    String schedule;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelter shelter = (Shelter) o;
        return Objects.equals(name, shelter.name) && Objects.equals(address, shelter.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }
}
