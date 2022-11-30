package ru.jd6team7.cooperatproject1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.exceptions.DailyReportEmptyListException;
import ru.jd6team7.cooperatproject1.exceptions.GuardsListIsEmptyException;
import ru.jd6team7.cooperatproject1.model.DailyReport;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.repository.DailyReportRepository;
import ru.jd6team7.cooperatproject1.repository.NotificationTaskRepository;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;

import java.sql.Array;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс, описывающий логику работы с сущностью Ежедневный отчёт (DailyReport)
 * позволяет просмотреть список ежедневных отчётов. Привязать отчёт к Питомцу и Приюту
 * откуда можно получить список задолжников и через бот-ассистена вручную направить уведомление
 * о недобросовестном отношении к отчёту
 */
@Service
public class DailyReportService {
    /** Объект репозитория для работы с данными, хранящимися в БД */
    private final NotificationTaskRepository notifTaskRepo;
    /** Объект репозитория для работы с данными, хранящимися в БД */
    private final DailyReportRepository dailyReportRepo;
    /** Объект репозитория для работы с данными, хранящимися в БД */
    private final TryPeriodRepository tryPeriodRepo;
    /** Объект Логера для вывода лог-сообщений в файл лог-журнала */
    private final Logger logger = LoggerFactory.getLogger("ru.telbot.file");
    /** Строка-констната Предупреждения Опекуну от команды Волонтёров */
    private final String WARNING_MSG = "Дорогой усыновитель, мы заметили, " +
            "что ты заполняешь отчет не так подробно, как необходимо. " +
            "Пожалуйста, подойди ответственнее к этому занятию. " +
            "*В противном случае волонтеры приюта будут обязаны самолично* " +
            "*проверять условия содержания животного*";

    public DailyReportService(NotificationTaskRepository notifTaskRepo,
                              DailyReportRepository dailyReportRepo,
                              TryPeriodRepository tryPeriodRepo) {
        this.notifTaskRepo = notifTaskRepo;
        this.dailyReportRepo = dailyReportRepo;
        this.tryPeriodRepo = tryPeriodRepo;
    }

    /**
     * Метод возвращает записи Ежедневных отчётов от Опекунов из БД
     * на текущую дату для данного Приюта
     * @param inpShelterID
     * @return Возвращает список экземпляров Отчётов или выбрасывает исключение
     */
    public List<DailyReport> showDailyReports(Integer inpShelterID) {
        logger.debug("Вызван метод showDailyReports с inpShelterID = " + inpShelterID);
        List<DailyReport> resultList = dailyReportRepo.getCurrentDateDailyReports(inpShelterID);
        if (resultList.isEmpty()){
            throw new DailyReportEmptyListException("Список Ежедневных отчётов на сегодня Пуст!");
        } else {
            return resultList;
        }
    }

    private DogVisitor convertToDogVisitor(Object[] inpObj){
        DogVisitor dogVis = new DogVisitor();
        dogVis.setId(Long.decode(inpObj[0].toString()));
        if (inpObj[1]!=null) { dogVis.setName(inpObj[1].toString()); }
        if (inpObj[2]!=null) { dogVis.setPhoneNumber(inpObj[2].toString()); }
        if (inpObj[3]!=null) { dogVis.setEmail(inpObj[3].toString()); }
        if (inpObj[4]!=null) { dogVis.setChatId(Long.decode(inpObj[4].toString())); }
        return dogVis;
    }

    /**
     * Метод возвращает список Опекунов для данного Приюта из БД
     * @param inpShelterID
     * @return Возвращает список экземпляров Опекунов или выбрасывает исключение
     */
    public List<DogVisitor> showGuardsList(Integer inpShelterID) {
        logger.debug("Вызван метод showGuardsList с inpShelterID = " + inpShelterID);
        List<Object[]> notifVisitors = tryPeriodRepo.getInfoAboutVisitorTryPeriods(inpShelterID);
        if (notifVisitors.isEmpty()){
            throw new GuardsListIsEmptyException("Список Опекунов Пуст!");
        } else {
            List<DogVisitor> dogVisList = new ArrayList<DogVisitor>();
            for (Object[] notifVisitor : notifVisitors) {
                dogVisList.add(convertToDogVisitor(notifVisitor));
            }
            return dogVisList;
        }
    }

    /**
     * Метод отправляет Предупреждение указанному Опекуну (функция Волонтёров)
     * @param inpChartID
     */
    public void makeWarningForDebtor(Long inpChartID) {
        logger.debug("Вызван метод makeWarningForDebtor с inpChartID = " + inpChartID);
        notifTaskRepo.addNotifOnDate(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                WARNING_MSG,
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(2L),
                inpChartID
        );
    }
}
