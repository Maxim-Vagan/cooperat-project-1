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
public class Cat {
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
        if (!(o instanceof Cat)) return false;
        Cat cat = (Cat) o;
        return Objects.equals(getShelterID(), cat.getShelterID()) && Objects.equals(getPetID(), cat.getPetID()) && Objects.equals(getPetName(), cat.getPetName()) && Objects.equals(getAnimalGender(), cat.getAnimalGender()) && Objects.equals(getAge(), cat.getAge()) && Objects.equals(getCurrentState(), cat.getCurrentState());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShelterID(), getPetID(), getPetName(), getAnimalGender(), getAge(), getCurrentState());
    }

    @Override
    public String toString() {
        return "Cat{" +
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
