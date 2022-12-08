package ru.jd6team7.cooperatproject1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.exceptions.TryPeriodNotFoundException;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.model.TryPeriod;
import ru.jd6team7.cooperatproject1.repository.NotificationTaskRepository;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final TryPeriodRepository tryPeriodRepo;
    private final NotificationTaskRepository notifTaskRepo;
    private final DogService petService;
    /** Объект Логера для вывода лог-сообщений в файл лог-журнала */
    private final Logger logger = LoggerFactory.getLogger("ru.telbot.file");
    /** Константа Json формата */
    private final String JSON_ROW_REPORT = """
            {"visitor": {"name": null, "phone": null, "email": null},
            "tryPeriods": []}""";
    /** Константа Json формата */
    private final String TRY_PERIOD_ROW = """
            "tryPeriod": {
            "startDate": null,
            "endDate": null,
            "periodStatus": null,
            "apEndDate": null,
            "reasonDescription": null,
            "pet": {"ID": null, "name": null}
            }""";
    /** Конструктор */
    public TryPeriodService(TryPeriodRepository tryPeriodRepo,
                            NotificationTaskRepository notifTaskRepo,
                            DogService petService) {
        this.tryPeriodRepo = tryPeriodRepo;
        this.notifTaskRepo = notifTaskRepo;
        this.petService = petService;
    }

    /**
     * Метод добавления записи о ИС для указанного Посетителя в БД
     * @param inpTryP
     * @return Возвращает созданный экземпляр ИС
     */
    public TryPeriod addTryPeriodToVisitor(TryPeriod inpTryP) {
        logger.debug("Вызван метод addTryPeriodToVisitor с inpTryP = " + inpTryP.getPetID() + " >>> " + inpTryP.getVisitorID());
        String petTableName = tryPeriodRepo.getTableName(inpTryP.getShelterID(), inpTryP.getPetID());
        switch (petTableName) {
            case "Dog" -> petService.putDogState(inpTryP.getPetID(), PetState.WITH_GUARDIAN);
/*            case "Cat" -> petService.putCatState(inpTryP.getPetID(), PetState.WITH_GUARDIAN);*/
            default -> logger.debug("WARN - Не удалось определить тип Питомца!");
        }
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

    //getListDebtorsNotification("vis.*");
    /**
     * Метод возвращает список сводных данных о всех ИС для указанного Посетителя из БД
     * @param inpShelterID
     * @return Возвращает список массива данных об Опекуне, его ИС и Питомце.
     * Или выбрасывает исключение
     */
    public List<Object[]> showPivotTryPeriodsReport(Integer inpShelterID){
        logger.debug("Вызван метод showPivotTryPeriodsReport с inpShelterID = " + inpShelterID);
        List<Object[]> resultList = tryPeriodRepo.getInfoAboutVisitorTryPeriods(inpShelterID);
        if (resultList.isEmpty()){
            throw new TryPeriodNotFoundException("Список Опекунов данного Приюта (ИД = )" + inpShelterID + " Пуст!");
        } else {
            return resultList;
        }
    }

    /**
     * Метод обновляет статус существующего указанного ИС в БД
     * @param inpTryP
     * @param newStatus
     * @return Возвращает обновлённый по статусу экземпляр ИС
     */
    private TryPeriod updateTryPeriodStatus(TryPeriod inpTryP, TryPeriod.TryPeriodStatus newStatus){
        String[] infoParts = tryPeriodRepo.getGuardianInfoForNotify(inpTryP.getId()).split(";");
        int extendedDays = 0;
        if (inpTryP.getAdditionalEndDate() != null) {
            extendedDays = inpTryP.getAdditionalEndDate().compareTo(inpTryP.getEndDate())/(1000*3600*24);
        }
        switch (newStatus) {
            case PASSED -> notifTaskRepo.addNotifOnDate(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    String.format("Уважаемый(ая) %s поздравляем! Ваш Испытательный срок пройден!", infoParts[1]),
                    LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1L),
                    Long.decode(infoParts[0])
            );
            case EXTENDED -> notifTaskRepo.addNotifOnDate(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    String.format("""
                            Уважаемый(ая) %s сообщаем
                            что Ваш Испытательный срок продлён на %s дней""", infoParts[1], extendedDays),
                    LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1L),
                    Long.decode(infoParts[0])
            );
            case FAILED -> notifTaskRepo.addNotifOnDate(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    String.format("""
                            Уважаемый(ая) %s вынуждены сообщить
                            что Ваш Испытательный срок Не пройден!
                            Для разъяснения причин Вы можете связаться с волонтёром
                            %s %s по номеру %s""", infoParts[1], infoParts[2], infoParts[3], infoParts[4]),
                    LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1L),
                    Long.decode(infoParts[0])
            );
        }
        inpTryP.setStatus(newStatus);
        return inpTryP;
    }

    /**
     * Метод обновления записи о ИС для указанного Посетителя в БД
     * @param inpTryP
     * @return Возвращает обновлённый экземпляр ИС
     */
    public TryPeriod updateTryPeriod(TryPeriod inpTryP){
        TryPeriod resultEntity = tryPeriodRepo.findById(inpTryP.getId()).orElse(null);
        if (resultEntity!= null){
            resultEntity.setAdditionalEndDate(inpTryP.getAdditionalEndDate());
            resultEntity.setReasonDescription(inpTryP.getReasonDescription());
            tryPeriodRepo.save(updateTryPeriodStatus(resultEntity, inpTryP.getStatus()));
            return resultEntity;
        } else { throw new TryPeriodNotFoundException("Не найден обновляемый ИС по его ИД номеру"); }
    }
}
