package ru.jd6team7.cooperatproject1.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.model.NotificationTask;
import ru.jd6team7.cooperatproject1.repository.NotificationTaskRepository;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CheckAndNotify {
    /** Объект репозитория для записи в БД данных по Уведомлениям */
    private final NotificationTaskRepository notifTaskRepo;
    /** Объект репозиторий для получения данных из БД по ИС */
    private final TryPeriodRepository tryPeriodRepo;
    /** Объект Телеграм бот-ассистента */
    private final TelegramBot telBot;
    /** Значение ChatID для чата с Волонтёрами(ом) для уведомления о задолженности */
    private final Long dutyVolunteer = 1805591065L;
    /** Объект Логера для вывода лог-сообщений в файл лог-журнала */
    private final Logger loggerFile = LoggerFactory.getLogger("ru.telbot.file");
    /** Строка-констната Уведомлений Опекуну от Бот-ассистента */
    private final String NOTIFY_MSG = "Дорогой опекун, напоминаем о необходимости " +
            "прислать *Ежедневный отчёт* по каждому из питомцев под вашей опекой! " +
            "Пока не завершён испытательный срок";

    public CheckAndNotify(NotificationTaskRepository notifTaskRepo,
                          TryPeriodRepository tryPeriodRepo,
                          TelegramBot telBot) {
        this.notifTaskRepo = notifTaskRepo;
        this.tryPeriodRepo = tryPeriodRepo;
        this.telBot = telBot;
    }

    public void startChecking(){
        loggerFile.debug("Starting Check Scheduling");
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void runScheduledNotificationProc(){
        String NowMinute = LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        List<NotificationTask> notificationList = notifTaskRepo.getNotification(NowMinute);


        if (notificationList.size() > 0){
            loggerFile.debug("Scheduling Autosend Notifications on current time ({})", NowMinute);
            notificationList.forEach(notify -> {
                SendMessage messageToChat = new SendMessage(notify.getTelegramChatID(),
                        notify.getMessageText()).parseMode(ParseMode.Markdown);
                SendResponse responseFromBot = telBot.execute(messageToChat);
            });
        }
    }

    @Scheduled(cron = "${daily.reports.checking.time}")
    public void runCheckForDailyRepsProc(){
        String NowMinute = LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        List<String> notifChartIDs = tryPeriodRepo.getListQuardiansForDailyNotification("vis.chart_id");
        for (String notifChartID : notifChartIDs) {
            notifTaskRepo.addNotifOnDate(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    NOTIFY_MSG,
                    LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1L),
                    Long.decode(notifChartID)
            );
        }
        List<String> notifVisitors = tryPeriodRepo.getListDebtorsNotification();
        if (notifVisitors.size()>0) {
            StringBuilder sb = new StringBuilder();
            sb.append("*Список опекунов-должников*").append("\r\n");
            for (String notifVisitor : notifVisitors) {
                sb.append(notifVisitor).append("\r\n");
            }
            notifTaskRepo.addNotifOnDate(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    sb.toString(),
                    LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(2L),
                    dutyVolunteer
            );
        }
    }
}
