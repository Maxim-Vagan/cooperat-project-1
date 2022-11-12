package ru.jd6team7.cooperatproject1.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BotUpdatesListener implements UpdatesListener {
    private final Logger loggerConsole = LoggerFactory.getLogger("ru.telbot.console");
    private final Logger loggerFile = LoggerFactory.getLogger("ru.telbot.file");
    private boolean dasNeedToTreateNotifies = false;
    private String INTRO_INFO = "Здравствуйте! Я - бот-ассистент\r\nМоя задача - помочь новым посетителям\r\n" +
            "получить основную информацию о Приюте, заказать пропуск для посещения, оставить контактную информацию\r\n" +
            "Или обратиться за помощью к волонтёрам";

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /** Метод обработки
     *
     * @param updates
     * @return
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            String messageText = null;
            LocalDateTime sendTime = LocalDateTime.now();
            LocalDateTime justNow = sendTime.truncatedTo(ChronoUnit.SECONDS);
            String content = update.message().text();
            long chatId = update.message().chat().id();
            loggerConsole.info("Processing Chat update:In chat \"{}\" From \"{}\" came message - \"{}\"",
                    update.message().chat().firstName()+" "+update.message().chat().lastName(),
                    update.message().from().firstName()+" "+update.message().from().lastName(),
                    content);
            if (content.contains("/start")) {
                messageText = "Принята команда /start. Для вызова справки отправьте /help";
                dasNeedToTreateNotifies = true;
            } else if (content.contains("/help")) {
                messageText = """
                        Список форматов команд чат боту:\r\n
                        /add_task DD.MM.YYYY HH24:MI Текст уведомления\r\n
                        где /add_task тип команды для добавления уведомления в журнал заданий;\r\n
                        DD.MM.YYYY HH24:MI формат даты и времени, когда следует отправить уведомление собеседнику;\r\n
                        Текст уведомления - сама фраза уведомления, которую следует при выполнении задания отправить в чат собеседнику;\r\n
                        /show_tasks DD.MM.YYYY HH24\r\n
                        где /show_tasks тип команды для вывода уведомлений в журнале заданий, которые назначены на указанную дату в указанный час;
                        DD.MM.YYYY HH24 формат даты и часа, назначенных заданий\r\n
                        /help - команда вывода подсказки\r\n
                        /start - включение режима обработки команд\r\n
                        /end - выключение режима обработки команд
                        """;
            } else if ((content.contains("/add_task") || content.contains("/show_tasks")) && dasNeedToTreateNotifies) {
                String notification = parseNotificationTask(content);
                if (notification.startsWith("ERROR")) {
                    messageText = notification.replaceAll("ERROR", "\uD83D\uDE22 ");
                } else if (content.contains("/add_task")) {
                    sendTime = LocalDateTime.parse(notification.split("&")[0], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    notifTaskRepo.addNotifOnDate(justNow,
                            notification.split("&")[1],
                            sendTime,
                            update.message().chat().id());
                    messageText = "Принята команда /add_task. Задание уведомления установлено на\r\n" +
                            sendTime.truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                } else if (content.contains("/show_tasks")){
                    List<String> recSet = notifTaskRepo.getNotifOnDate(notification);
                    if (recSet.size() > 0) messageText = String.join("\r\n", recSet);
                    else messageText = "На выбранную дату и час ничего не запланировано!";
                }
            } else if (content.contains("/end")) {
                messageText = "Принята команда /end. Для вызова справки отправьте /help";
                dasNeedToTreateNotifies = false;
            }
            if (messageText != null){
                SendMessage messageToChat = new SendMessage(chatId, messageText);
                SendResponse responseFromBot = telegramBot.execute(messageToChat);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
