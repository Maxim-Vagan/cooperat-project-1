package ru.jd6team7.cooperatproject1.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


/** Сущность "Испытательный срок" хранит реестр испытательных сроков для Посетителей,
 * которые имеют на руках опекаемых Питомцев. Содержит информацию:
 * личный ИД номер питомца; дату начала испытательного срока; дату окончания; статус окончания исп. срока
 * При продлении периода, может хранить комментарий Волонтёра и его ИД номер для связи
 * с реестром Волонтёров.
 * */
@Entity(name = "try_period_registry")
@Getter
@Setter
public class TryPeriod {

    public static enum TryPeriodStatus{
        ACTIVE("действует"),
        PASSED("пройден"),
        EXTENDED("продлён"),
        FAILED("не пройден"),
        CANCELED("отменён");

        private final String description;

        TryPeriodStatus(String description) {
            this.description = description;
        }

        public String getCode() {
            return description;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pet_id")
    private Long petID;
    @Column(name = "visitor_id")
    private Integer visitorID;
    @Column(name = "shelter_id")
    private Integer shelterID;
    @Column(name = "volunteer_id")
    private Integer volunteerID;
    @Column(name = "try_period_status_id")
    @Enumerated(EnumType.STRING)
    private TryPeriodStatus status;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "additional_period_end_date")
    private LocalDateTime additionalEndDate;
    @Column(name = "reason_description")
    private String reasonDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TryPeriod)) return false;
        TryPeriod tryPeriod = (TryPeriod) o;
        return Objects.equals(getPetID(), tryPeriod.getPetID()) && Objects.equals(getShelterID(), tryPeriod.getShelterID()) && Objects.equals(getVisitorID(), tryPeriod.getVisitorID()) && Objects.equals(getVolunteerID(), tryPeriod.getVolunteerID()) && getStatus() == tryPeriod.getStatus() && Objects.equals(getStartDate(), tryPeriod.getStartDate()) && Objects.equals(getEndDate(), tryPeriod.getEndDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPetID(), getShelterID(), getVisitorID(), getVolunteerID(), getStatus(), getStartDate(), getEndDate());
    }

    @Override
    public String toString() {
        return "TryPeriod{" +
                "id=" + id +
                ", petID=" + petID +
                ", visitorID=" + visitorID +
                ", shelterID=" + shelterID +
                ", volunteerID=" + volunteerID +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", additionalEndDate=" + additionalEndDate +
                ", reasonDescription='" + reasonDescription + '\'' +
                '}';
    }
}
