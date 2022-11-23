package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jd6team7.cooperatproject1.model.Pet;
import ru.jd6team7.cooperatproject1.model.TryPeriod;
import ru.jd6team7.cooperatproject1.model.Visitor;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
public interface TryPeriodRepository extends JpaRepository<TryPeriod, Long> {

    /** Показать всех Посетителей данного Приюта */
    @Query(value = "select * from visitor " +
            "inner join visitors_and_shelters vas " +
            "on visitor.id = vas.visitor_id and vas.id = :shelter_id", nativeQuery = true)
    List<Visitor> getAllVisitorsOfShelter(@Param("shelter_id") Integer shelterID);

    /** Показать все действующие Испытательные периоды указанного Посетителя данного Приюта */
    @Query(value = "select * from try_period_registry tpr " +
            "inner join visitors_and_shelters vas on tpr.visitor_id = vas.visitor_id " +
            "where vas.visitor_id = :visitor_id " +
            "and vas.shelter_id = :shelter_id " +
            "and tpr.end_date is not null " +
            "and coalesce(tpr.additional_period_end_date, tpr.end_date) > now()", nativeQuery = true)
    List<TryPeriod> getTryPeriodsOfVisitor(@Param("shelter_id") Integer shelterID,
                                           @Param("visitor_id") Integer visitorID);

    /** Показать сводную информацию по Испытательным срокам Посетителя данного Приюта */
    @Query(value = "select distinct vis.name, " +
            "vis.phone_number, " +
            "vis.email, " +
            "tpr.start_date, " +
            "tpr.end_date, " +
            "tpr.try_period_status_id, " +
            "tpr.additional_period_end_date, " +
            "tpr.reason_description, " +
            "p.pet_id, " +
            "p.pet_name " +
            "from visitor vis " +
            "inner join visitors_and_shelters vas on vis.id = vas.visitor_id " +
            "inner join try_period_registry tpr on vis.id = tpr.visitor_id " +
            "inner join pet p on tpr.pet_id = p.pet_id " +
            "where vas.visitor_id = :visitor_id " +
            " and vas.shelter_id = :shelter_id", nativeQuery = true)
    List<Object[]> getPivotInfoAboutVisitorTryPeriods(@Param("shelter_id") Integer shelterID,
                                                     @Param("visitor_id") Integer visitorID);
}
