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
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pet_id")
    private Long petID;
    @Column(name = "pet_name")
    private String petName;
    @Column(name = "animal_gender")
    private String animalGender;
    private Integer age;
    @Column(name = "current_state")
    private String currentState;
    @Column(name = "path_file_to_photo")
    private String pathFileToPhoto;
    @Column(name = "shelter_id")
    private Integer shelterID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dog)) return false;
        Dog dog = (Dog) o;
        return Objects.equals(getShelterID(), dog.getShelterID()) && Objects.equals(getPetID(), dog.getPetID()) && Objects.equals(getPetName(), dog.getPetName()) && Objects.equals(getAnimalGender(), dog.getAnimalGender()) && Objects.equals(getAge(), dog.getAge()) && Objects.equals(getCurrentState(), dog.getCurrentState());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShelterID(), getPetID(), getPetName(), getAnimalGender(), getAge(), getCurrentState());
    }

    @Override
    public String toString() {
        return "Dog{" +
                "petID=" + petID +
                ", petName='" + petName + '\'' +
                ", animalGender='" + animalGender + '\'' +
                ", age=" + age +
                ", currentState='" + currentState + '\'' +
                ", pathFileToPhoto='" + pathFileToPhoto + '\'' +
                ", shelterID=" + shelterID +
                '}';
    }
}
