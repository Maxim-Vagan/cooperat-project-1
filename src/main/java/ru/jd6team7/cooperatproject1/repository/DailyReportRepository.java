package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jd6team7.cooperatproject1.model.DailyReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    /** Показать все Ежедневные отчёты по данному Приюту */
    @Query(value = "select dr.* from daily_report dr " +
            "where to_char(dr.create_date, 'yyyy-MM-dd') = to_char(current_date, 'yyyy-MM-dd') " +
            "and dr.delete_date is null " +
            "and dr.shelter_id = :shelter_id", nativeQuery = true)
    List<DailyReport> getCurrentDateDailyReports(@Param("shelter_id") Integer shelterID);

    /** Найти Ежедневный отчёт по данному Приюту, Питомцу и дате создания */
    Optional<DailyReport> findById(Long reportID);

}
