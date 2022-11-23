package ru.jd6team7.cooperatproject1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tomcat.util.json.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.exceptions.TryPeriodNotFoundException;
import ru.jd6team7.cooperatproject1.model.Pet;
import ru.jd6team7.cooperatproject1.model.TryPeriod;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;

import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

/**
 * Класс, описывающий логику работы с сущностью Испытательный период (TryPeriod)
 * Через интерфейс репозитория {@link TryPeriodRepository} осуществляются
 * основные манипуляции с данными по Испытательным срокам.
 * Вывод списка ИС конкретного Опекуна, вывод сводного отчета по Опекуну.
 * Операции добавления, обновления и удаления ИС
 */
@Service
public class TryPeriodService {
    /** Объект репозитория для работы с данными, хранящимися в БД */
    private TryPeriodRepository tryPeriodRepo;
    /** Объект Логера для вывода лог-сообщений в файл лог-журнала */
    private final Logger logger = LoggerFactory.getLogger("ru.telbot.file");
    /** Константа Json формата */
    private final String JSON_ROW_REPORT = "{" +
            "\"visitor\": {\"name\": null, \"phone\": null, \"email\": null}," +
            "\"tryPeriods\": []}";
    /** Константа Json формата */
    private final String TRY_PERIOD_ROW = "tryPeriod: {" +
            "\"startDate\": null, " +
            "\"endDate\": null, " +
            "\"periodStatus\": null, " +
            "\"apEndDate\": null, " +
            "\"reasonDescription\": null, " +
            "\"pet\": {\"ID\": null, \"name\": null}" +
            "}";
    /** Конструктор */
    public TryPeriodService(TryPeriodRepository tryPeriodRepo) {
        this.tryPeriodRepo = tryPeriodRepo;
    }

    /**
     * Метод добавления записи о ИС для указанного Посетителя в БД
     * @param inpTryP
     * @return Возвращает созданный экземпляр ИС
     */
    public TryPeriod addTryPeriodToVisitor(TryPeriod inpTryP) {
        logger.debug("Вызван метод addTryPeriodToVisitor с inpTryP = " + inpTryP.getPetID() + " >>> " + inpTryP.getVisitorID());
        return tryPeriodRepo.save(inpTryP);
    }

    /**
     * Метод возвращает записи об ИС для указанного Посетителя из БД
     * @param inpShelterID
     * @param inpVisitorID
     * @return Возвращает список экземпляров ИС или выбрасывает исключение
     */
    public List<TryPeriod> findTryPeriodsOfVisitor(Integer inpShelterID, Integer inpVisitorID) {
        logger.debug("Вызван метод findTryPeriodsOfVisitor с inpShelterID = " + inpShelterID + " и inpVisitorID = " + inpVisitorID);
        List<TryPeriod> resultList = tryPeriodRepo.getTryPeriodsOfVisitor(inpShelterID, inpVisitorID);
        if (resultList.isEmpty()){
            throw new TryPeriodNotFoundException("Список ИС для указанного Посетителя (ИД = " +
                    + inpVisitorID + ") в данном Питомнике (ИД = )" + inpShelterID + " Пуст!");
        } else {
            return resultList;
        }
    }

    /**
     * Метод возвращает список сводных данных о всех ИС для указанного Посетителя из БД
     * @param inpShelterID
     * @param inpVisitorID
     * @return Возвращает список массива данных об Опекуне, его ИС и Питомце.
     * Или выбрасывает исключение
     */
    public ObjectNode showPivotTryPeriodsReport(Integer inpShelterID, Integer inpVisitorID) throws JsonProcessingException {
        ObjectNode reportJson = (ObjectNode) new ObjectMapper().readTree(String.valueOf(new JSONParser(JSON_ROW_REPORT)));
        ObjectNode tryPeriodRow = (ObjectNode) new ObjectMapper().readTree(String.valueOf(new JSONParser(TRY_PERIOD_ROW)));

        logger.debug("Вызван метод showPivotTryPeriodsReport с inpShelterID = " + inpShelterID + " и inpVisitorID = " + inpVisitorID);
        List<Object[]> resultList = tryPeriodRepo.getPivotInfoAboutVisitorTryPeriods(inpShelterID, inpVisitorID);
        if (resultList.isEmpty()){
            throw new TryPeriodNotFoundException("Список ИС для указанного Посетителя (ИД = " +
                    + inpVisitorID + ") в данном Питомнике (ИД = )" + inpShelterID + " Пуст!");
        } else {
            for (Object[] reportDetail : resultList) {
                Visitor vst = (Visitor) reportDetail[0];
                TryPeriod trp = (TryPeriod) reportDetail[1];
                Pet pet = (Pet) reportDetail[2];
                reportJson.with("visitor").put("name", vst.getName());
                reportJson.with("visitor").put("phone", vst.getPhoneNumber());
                reportJson.with("visitor").put("email", vst.getEmail());
                tryPeriodRow.with("tryPeriod").put("startDate",
                        trp.getStartDate()
                                .truncatedTo(ChronoUnit.SECONDS)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                tryPeriodRow.with("tryPeriod").put("endDate",
                        trp.getEndDate()
                                .truncatedTo(ChronoUnit.SECONDS)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                tryPeriodRow.with("tryPeriod").put("periodStatus", String.valueOf(trp.getStatus()));
                tryPeriodRow.with("tryPeriod").put("apEndDate",
                        trp.getAdditionalEndDate()
                                .truncatedTo(ChronoUnit.SECONDS)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                tryPeriodRow.with("tryPeriod").put("reasonDescription", trp.getReasonDescription());
                tryPeriodRow.with("tryPeriod").with("pet").put("ID", pet.getPetID());
                tryPeriodRow.with("tryPeriod").with("pet").put("name", pet.getPetName());
                reportJson.withArray("tryPeriods").add(tryPeriodRow);
            }
            return reportJson;
        }
    }
}
