package ru.jd6team7.cooperatproject1.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/** Сущность "Ежедневный отчёт Опекуна" хранит ежедневную информацию о состоянии Питомца.
 * Присылается Посетителем и содержит:
 * Текстовую часть (например: состояние здоровья, рацион, привычки).
 * Изображение (Фото питомца)
 * */
@Entity
@Getter
@Setter
public class DailyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "pet_id")
    private Long petID;
    @Column(name = "shelter_id")
    private Integer shelterID;
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "delete_date")
    private LocalDateTime deleteDate;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "media_type")
    private String mediaType;
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] photo;
    @Column(name = "path_file_to_photo")
    private String pathToFile;
    @Column(name = "day_diet")
    private String dayDiet;
    @Column(name = "main_health")
    private String mainHealth;
    @Column(name = "old_hebits")
    private String oldHebits;
    @Column(name = "new_hebits")
    private String newHebits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyReport)) return false;
        DailyReport that = (DailyReport) o;
        return getPetID().equals(that.getPetID()) && Objects.equals(getPathToFile(), that.getPathToFile()) && Objects.equals(getDayDiet(), that.getDayDiet()) && Objects.equals(getMainHealth(), that.getMainHealth()) && Objects.equals(getOldHebits(), that.getOldHebits()) && Objects.equals(getNewHebits(), that.getNewHebits());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPetID(), getPathToFile(), getDayDiet(), getMainHealth(), getOldHebits(), getNewHebits());
    }

    @Override
    public String toString() {

        return "DailyReport{" +
                "petID=" + petID +
                ", createDate=" + createDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                ", pathToFile='" + pathToFile + '\'' +
                ", dayDiet='" + dayDiet + '\'' +
                ", mainHealth='" + mainHealth + '\'' +
                ", oldHebits='" + oldHebits + '\'' +
                ", newHebits='" + newHebits + '\'' +
                '}';
    }
}
