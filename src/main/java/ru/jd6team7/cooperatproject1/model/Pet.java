package ru.jd6team7.cooperatproject1.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    @Column(name = "pet_name")
    String petName;
    @Column(name = "animal_kind")
    String animalKind;
    @Column(name = "animal_gender")
    String animalGender;
    Integer age;
    @Column(name = "current_state")
    String currentState;
    @Column(name = "path_file_to_photo")
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

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", petName='" + petName + '\'' +
                ", animalKind='" + animalKind + '\'' +
                ", animalGender='" + animalGender + '\'' +
                ", age=" + age +
                ", currentState='" + currentState + '\'' +
                ", pathFileToPhoto='" + pathFileToPhoto + '\'' +
                '}';
    }
}
