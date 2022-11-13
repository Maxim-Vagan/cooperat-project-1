package ru.jd6team7.cooperatproject1.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/*
Сущность "Питомец" хранит необходимую информацию о Питомце:
Кличка, пол, возраст, текущий статус (У посетителя, В приюте, У опекуна, Усыновлён и т.п.)
*/
@Entity
@Getter
@Setter
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String petName;
    String animalKind;
    String animalGender;
    Integer age;
    String currentState;
    String pathFileToPhoto;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet)) return false;
        Pet pet = (Pet) o;
        return Objects.equals(getPetName(), pet.getPetName()) && Objects.equals(getAnimalKind(), pet.getAnimalKind()) && Objects.equals(getAnimalGender(), pet.getAnimalGender()) && Objects.equals(getAge(), pet.getAge()) && Objects.equals(getCurrentState(), pet.getCurrentState());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPetName(), getAnimalKind(), getAnimalGender(), getAge(), getCurrentState());
    }
}
