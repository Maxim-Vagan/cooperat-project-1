package ru.jd6team7.cooperatproject1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/*
Сущность "Опекун" хранит информацию об "Усыновителях" питомцев из приюта.
ФИО, Контактные данные для связи, Фактический адрес содержания питомца
*/
@Entity
public class Guardian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String guardianName;
    String guardianSurname;
    String guardianLastname;
    String phoneNumber;
    String email;
    String actualAddress;
    String telegramChatAlias;

    public Long getId() {
        return id;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianSurname() {
        return guardianSurname;
    }

    public void setGuardianSurname(String guardianSurname) {
        this.guardianSurname = guardianSurname;
    }

    public String getGuardianLastname() {
        return guardianLastname;
    }

    public void setGuardianLastname(String guardianLastname) {
        this.guardianLastname = guardianLastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActualAddress() {
        return actualAddress;
    }

    public void setActualAddress(String actualAddress) {
        this.actualAddress = actualAddress;
    }

    public String getTelegramChatAlias() {
        return telegramChatAlias;
    }

    public void setTelegramChatAlias(String telegramChatAlias) {
        this.telegramChatAlias = telegramChatAlias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guardian)) return false;
        Guardian guardian = (Guardian) o;
        return Objects.equals(getGuardianName(), guardian.getGuardianName()) && Objects.equals(getGuardianSurname(), guardian.getGuardianSurname()) && Objects.equals(getGuardianLastname(), guardian.getGuardianLastname()) && Objects.equals(getPhoneNumber(), guardian.getPhoneNumber()) && Objects.equals(getEmail(), guardian.getEmail()) && Objects.equals(getTelegramChatAlias(), guardian.getTelegramChatAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuardianName(), getGuardianSurname(), getGuardianLastname(), getPhoneNumber(), getEmail(), getTelegramChatAlias());
    }
}
