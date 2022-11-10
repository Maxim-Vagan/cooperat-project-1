package ru.jd6team7.cooperatproject1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/*
Сущность "Приют" хранит необходимую информацию об организации:
Текст описания деятельности, График работы, Справочную инфу.
*/
@Entity
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String organizationName;
    String legalName;
    Integer INN;
    String directorFIO;
    Long infoAboutID;
    String address;
    String mapTrackSchemaImageFile;
    Long workingSchedualeID;

    public Long getId() {
        return id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public Integer getINN() {
        return INN;
    }

    public void setINN(Integer INN) {
        this.INN = INN;
    }

    public String getDirectorFIO() {
        return directorFIO;
    }

    public void setDirectorFIO(String directorFIO) {
        this.directorFIO = directorFIO;
    }

    public Long getInfoAboutID() {
        return infoAboutID;
    }

    public void setInfoAboutID(Long infoAboutID) {
        this.infoAboutID = infoAboutID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMapTrackSchemaImageFile() {
        return mapTrackSchemaImageFile;
    }

    public void setMapTrackSchemaImageFile(String mapTrackSchemaImageFile) {
        this.mapTrackSchemaImageFile = mapTrackSchemaImageFile;
    }

    public Long getWorkingSchedualeID() {
        return workingSchedualeID;
    }

    public void setWorkingSchedualeID(Long workingSchedualeID) {
        this.workingSchedualeID = workingSchedualeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shelter)) return false;
        Shelter shelter = (Shelter) o;
        return Objects.equals(getLegalName(), shelter.getLegalName()) && Objects.equals(getINN(), shelter.getINN()) && Objects.equals(getDirectorFIO(), shelter.getDirectorFIO()) && Objects.equals(getAddress(), shelter.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLegalName(), getINN(), getDirectorFIO());
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "organizationName='" + organizationName + '\'' +
                ", INN=" + INN +
                ", directorFIO='" + directorFIO + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
