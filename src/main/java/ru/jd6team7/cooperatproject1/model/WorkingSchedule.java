package ru.jd6team7.cooperatproject1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/*
Сущность "График работы Приюта" хранит информацию о рабочих днях.
*/
@Entity
public class WorkingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long workingScheduleID;
    String weekDayNameLong;
    Short weekDayNameShort;
    String startWorkDayTime;
    String finishWorkDayTime;
    Boolean isHoliday;

    public Long getId() {
        return id;
    }

    public Long getWorkingScheduleID() {
        return workingScheduleID;
    }

    public void setWorkingSchedualeID(Long workingScheduleID) {
        this.workingScheduleID = workingScheduleID;
    }

    public String getWeekDayNameLong() {
        return weekDayNameLong;
    }

    public void setWeekDayNameLong(String weekDayNameLong) {
        this.weekDayNameLong = weekDayNameLong;
    }

    public Short getWeekDayNameShort() {
        return weekDayNameShort;
    }

    public void setWeekDayNameShort(Short weekDayNameShort) {
        this.weekDayNameShort = weekDayNameShort;
    }

    public String getStartWorkDayTime() {
        return startWorkDayTime;
    }

    public void setStartWorkDayTime(String startWorkDayTime) {
        this.startWorkDayTime = startWorkDayTime;
    }

    public String getFinishWorkDayTime() {
        return finishWorkDayTime;
    }

    public void setFinishWorkDayTime(String finishWorkDayTime) {
        this.finishWorkDayTime = finishWorkDayTime;
    }

    public Boolean getHoliday() {
        return isHoliday;
    }

    public void setHoliday(Boolean holiday) {
        isHoliday = holiday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkingSchedule)) return false;
        WorkingSchedule that = (WorkingSchedule) o;
        return Objects.equals(getWorkingScheduleID(), that.getWorkingScheduleID()) && Objects.equals(getWeekDayNameLong(), that.getWeekDayNameLong()) && Objects.equals(getWeekDayNameShort(), that.getWeekDayNameShort()) && Objects.equals(getStartWorkDayTime(), that.getStartWorkDayTime()) && Objects.equals(getFinishWorkDayTime(), that.getFinishWorkDayTime()) && Objects.equals(isHoliday, that.isHoliday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkingScheduleID(), getWeekDayNameLong(), getWeekDayNameShort(), getStartWorkDayTime(), getFinishWorkDayTime(), isHoliday);
    }

}
