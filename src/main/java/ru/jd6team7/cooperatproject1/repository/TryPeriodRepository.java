package ru.jd6team7.cooperatproject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jd6team7.cooperatproject1.model.TryPeriod;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;

import java.util.List;

@Repository
public interface TryPeriodRepository extends JpaRepository<TryPeriod, Long> {

    final String COMMON_SQL_QUERY = """
            select distinct tpr.* from try_period_registry tpr
            inner join (select d.pet_id, d.pet_name, d.shelter_id ,'Dog' as kind from dog d
                        union select c.pet_id, c.pet_name, c.shelter_id ,'Cat' as kind from cat c
            ) pet on pet.pet_id = tpr.pet_id and pet.shelter_id = tpr.shelter_id
            inner join (select dv.* from dog_visitor dv
                union select cv.* from cat_visitor cv) guard on guard.id = tpr.visitor_id
            inner join visitor vis on vis.chat_id = guard.chat_id
            inner join visitors_and_shelters vas on guard.id = vas.visitor_id
            inner join shelter s on s.id = vas.shelter_id
                                        and s.id = tpr.shelter_id
            left join (select dr.pet_id, dr.shelter_id from daily_report dr
                        where to_char(dr.create_date, 'yyyy-MM-dd') = to_char(current_date, 'yyyy-MM-dd')
                            and dr.delete_date is null) dr on dr.pet_id = tpr.pet_id and dr.shelter_id = tpr.shelter_id
            where tpr.end_date is not null
              and tpr.try_period_status_id in ('ACTIVE', 'EXTENDED')
              and dr.pet_id is null
              and coalesce(tpr.additional_period_end_date, tpr.end_date) > now()
            """;
    final String COMMON_SQL_QUERY_2 = """
            select distinct vis.chat_id from try_period_registry tpr
            inner join (select d.pet_id, d.pet_name, d.shelter_id ,'Dog' as kind from dog d
                        union select c.pet_id, c.pet_name, c.shelter_id ,'Cat' as kind from cat c
            ) pet on pet.pet_id = tpr.pet_id and pet.shelter_id = tpr.shelter_id
            inner join (select dv.* from dog_visitor dv
                union select cv.* from cat_visitor cv) guard on guard.id = tpr.visitor_id
            inner join visitor vis on vis.chat_id = guard.chat_id
            inner join visitors_and_shelters vas on guard.id = vas.visitor_id
            inner join shelter s on s.id = vas.shelter_id
                                        and s.id = tpr.shelter_id
            left join (select dr.pet_id, dr.shelter_id from daily_report dr
                        where to_char(dr.create_date, 'yyyy-MM-dd') = to_char(current_date, 'yyyy-MM-dd')
                            and dr.delete_date is null) dr on dr.pet_id = tpr.pet_id and dr.shelter_id = tpr.shelter_id
            where tpr.end_date is not null
              and tpr.try_period_status_id in ('ACTIVE', 'EXTENDED')
              and dr.pet_id is null
              and coalesce(tpr.additional_period_end_date, tpr.end_date) > now()
            """;
    final String DEBTORS_SQL_QUERY = """
            select distinct
            concat(guard.id, '; ', guard.name, '; ', guard.phone_number, '; ', guard.email, '; ', guard.chat_id) as "visitor_info"
            from try_period_registry tpr
            inner join (select d.pet_id, d.pet_name, d.shelter_id ,'Dog' as kind from dog d
                     union select c.pet_id, c.pet_name, c.shelter_id ,'Cat' as kind from cat c
            ) pet on pet.pet_id = tpr.pet_id and pet.shelter_id = tpr.shelter_id
            inner join (select dv.* from dog_visitor dv
                     union select cv.* from cat_visitor cv) guard on guard.id = tpr.visitor_id
            inner join visitor vis on vis.chat_id = guard.chat_id
            inner join visitors_and_shelters vas on guard.id = vas.visitor_id
            inner join shelter s on s.id = vas.shelter_id and s.id = tpr.shelter_id
            left join (
                select dr.pet_id, dr.shelter_id,
                       (current_date - date(MAX(dr.create_date))) AS diff
                from daily_report dr
                where dr.delete_date is null
                GROUP BY dr.pet_id, dr.shelter_id
                HAVING (current_date - date(MAX(dr.create_date))) >= 2
                ) depts on depts.pet_id = tpr.pet_id and depts.shelter_id = tpr.shelter_id
            where tpr.end_date is not null
              and tpr.try_period_status_id in ('ACTIVE', 'EXTENDED')
              and depts.pet_id is not null
              and coalesce(tpr.additional_period_end_date, tpr.end_date) > now()
            """;
    final String GET_PET_TABLE_NAME = """
            select distinct pet.kind from
            (select d.pet_id, d.pet_name, d.shelter_id ,'Dog' as kind from dog d
                union select c.pet_id, c.pet_name, c.shelter_id ,'Cat' as kind from cat c
                ) pet
            inner join shelter s on s.id = pet.shelter_id
            where s.id = :shelter_id
            and pet.pet_id = :pet_id
            """;
    final String GUARDIAN_INFO = """
            select distinct
            concat(guard.chat_id, ';', guard.name, ';', vlr.surname, ';', vlr.name, ';', vlr.phone_number) as "info"
            from try_period_registry tpr
            inner join (select dv.* from dog_visitor dv
            union select cv.* from cat_visitor cv) guard on guard.id = tpr.visitor_id
            left join volunteer vlr on tpr.volunteer_id = vlr.id
            where tpr.id = :id
            """;
    final String DAILY_REPORT_PET_INFO = """
            select concat(
                           pet.pet_id, ';' ,pet.shelter_id, ';', pet.pet_name, '-', pet.pet_id, '-',
                pet.shelter_id, '-', to_char(now(), 'dd.MM.yyyy-hh24.mi.ss'), '.$'
                ) as file_info
            from try_period_registry tpr
                     inner join (select d.pet_id, d.pet_name, d.shelter_id ,'Dog' as kind from dog d
                                 union select c.pet_id, c.pet_name, c.shelter_id ,'Cat' as kind from cat c
            ) pet on pet.pet_id = tpr.pet_id and pet.shelter_id = tpr.shelter_id
                     inner join (select dv.* from dog_visitor dv
                                 union select cv.* from cat_visitor cv) guard on guard.id = tpr.visitor_id
                     inner join visitor vis on vis.chat_id = guard.chat_id
                     inner join visitors_and_shelters vas on guard.id = vas.visitor_id
                     inner join shelter s on s.id = vas.shelter_id and s.id = tpr.shelter_id
            where pet.pet_name like :pet_name
            and guard.chat_id = :chat_id
            """;
    /** Показать всех Посетителей данного Приюта */
    @Query(value = "select * from dog_visitor " +
            "inner join visitors_and_shelters vas " +
            "on dog_visitor.id = vas.visitor_id and vas.id = :shelter_id", nativeQuery = true)
    List<DogVisitor> getAllVisitorsOfShelter(@Param("shelter_id") Integer shelterID);

    /** Показать все действующие Испытательные периоды указанного Посетителя данного Приюта */
    @Query(value = """
            select distinct tpr.* from try_period_registry tpr
            where tpr.visitor_id = :visitor_id
                and tpr.shelter_id = :shelter_id
                and tpr.end_date is not null
                and coalesce(tpr.additional_period_end_date, tpr.end_date) > now()
            """, nativeQuery = true)
    List<TryPeriod> getTryPeriodsOfVisitor(@Param("shelter_id") Integer shelterID,
                                           @Param("visitor_id") Integer visitorID);

    /** Показать список тех ChatID Опекунов, кому нужно рассылать напоминание о ежедневном отчёте по опекаемым Питомцам */
    @Query(value = COMMON_SQL_QUERY_2, nativeQuery = true)
    List<String> getListQuardiansForDailyNotification(@Param("fields") String tabName);

    /** Показать список ИС тех Опекунов, кому нужно рассылать напоминание о ежедневном отчёте */
    @Query(value = COMMON_SQL_QUERY, nativeQuery = true)
    List<TryPeriod> getListTPForDailyNotification(@Param("fields") String tabName);

    /** Показать список тех Опекунов, кто не присылал отчёты Более 2 дней */
    @Query(value = DEBTORS_SQL_QUERY, nativeQuery = true)
    List<String> getListDebtorsNotification();

    /** Показать сводную информацию по Испытательным срокам Посетителя данного Приюта */
    @Query(value = "select distinct guard.* " +
            "from try_period_registry tpr " +
            "inner join (select dv.* from dog_visitor dv " +
            "union select cv.* from cat_visitor cv) guard on guard.id = tpr.visitor_id " +
            "inner join visitors_and_shelters vas on guard.id = vas.visitor_id " +
            "inner join shelter slt on slt.id = vas.shelter_id and slt.id = tpr.shelter_id " +
            "where vas.shelter_id = :shelter_id", nativeQuery = true)
    List<Object[]> getInfoAboutVisitorTryPeriods(@Param("shelter_id") Integer shelterID);

    /** Показать наименование таблицы Питомца по его ИД номеру и его ИД Приюта */
    @Query(value = GET_PET_TABLE_NAME, nativeQuery = true)
    String getTableName(@Param("shelter_id") Integer shelterID,
                        @Param("pet_id") Long petID);

    /** Показать информацию об Опекуне для уведомления его об изменённом статусе ИС */
    @Query(value = GUARDIAN_INFO, nativeQuery = true)
    String getGuardianInfoForNotify(@Param("id") Long ID);

    /** Найти информацию о Питомце по его кличке и chat_id его Опекуна  */
    @Query(value = DAILY_REPORT_PET_INFO, nativeQuery = true)
    String getPetInfoForDailyReport(@Param("pet_name") String petName,
                                    @Param("chat_id") Long chatID);
}
