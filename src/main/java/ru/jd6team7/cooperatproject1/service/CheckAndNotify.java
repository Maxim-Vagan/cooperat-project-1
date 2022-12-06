package ru.jd6team7.cooperatproject1.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.model.NotificationTask;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.repository.NotificationTaskRepository;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CheckAndNotify {

    @Autowired
    private NotificationTaskRepository notifTaskRepo;
    @Autowired
    private TryPeriodRepository tryPeriodRepo;

    private final TelegramBot telBot;

    private final Long dutyVolunteer = 1805591065L;
    /** Объект Логера для вывода лог-сообщений в файл лог-журнала */
    private final Logger loggerFile = LoggerFactory.getLogger("ru.telbot.file");
    /** Строка-констната Уведомлений Опекуну от Бот-ассистента */
    private final String NOTIFY_MSG = "Дорогой опекун, напоминаем о необходимости " +
            "прислать *Ежедневный отчёт* по каждому из питомцев под вашей опекой! " +
            "Пока не завершён испытательный срок";

    public CheckAndNotify(TelegramBot telBot) {
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
                SendMessage messageToChat = new SendMessage(notify.getTelegram_chat_id(),
                        notify.getMessage_text()).parseMode(ParseMode.Markdown);
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
                    LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(3L),
                    Long.decode(notifChartID)
            );
        }
        List<String> notifVisitors = tryPeriodRepo.getListDebtorsNotification();
        StringBuilder sb = new StringBuilder();
        sb.append("*Список опекунов-должников*").append("\r\n");
        for (String notifVisitor : notifVisitors) {
            sb.append(notifVisitor).append("\r\n");
        }
        notifTaskRepo.addNotifOnDate(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                sb.toString(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(5L),
                dutyVolunteer
        );
    }
}
