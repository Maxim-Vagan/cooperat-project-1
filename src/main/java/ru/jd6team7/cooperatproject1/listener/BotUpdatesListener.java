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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Service
public class BotUpdatesListener implements UpdatesListener {
    private final Logger loggerConsole = LoggerFactory.getLogger("ru.telbot.console");
    private final Logger loggerFile = LoggerFactory.getLogger("ru.telbot.file");
    private boolean dasNeedToTreateNotifies = false;
    private String INTRO_INFO = "\uD83D\uDC81 Здравствуйте! Я - бот-ассистент\r\n" +
            "Моя задача - помочь новым посетителям\r\n" +
            "* Узнать информацию о приюте (Этап 1 /info)\r\n" +
            "* Как взять питомца из приюта (Этап-2 /takePet)\r\n" +
            "* Прислать отчет о питомце (Этап-3 /sendReport)\r\n" +
            "* Позвать волонтёра (/help)";

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /** Метод анализирует входящую строку сообщения
     * <br> ~ При успешном выделении из строки целевого запроса пользователя
     * <br>выводится соответствующее контекстное меню
     * <br> ~ При невозможности определить целевой запрос
     * <br>выводится подсказка Бот-ассистента
     * @param inpMessage
     * @return
     */
    private String parseNotificationTask(String inpMessage){
        Pattern pattern1 = Pattern.compile("(/start|hi|Hello|[Пп]ривет|[Зз]дравствуй)",CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(inpMessage);
        String foundCommand = null;
        if (matcher1.matches()){
            foundCommand = matcher1.group(0);
        }
        if (foundCommand!=null) {
            return "/start";
        } else {
            return "/tip";
        }
    }

    /** Метод обработки сообщений в чате с бот-ассистентом.
     * <br>Производится логирование поступившего сообщения
     * <br>Его анализ и дальнейшее действие бота.
     * <br><i><b>Например:</b> Вывод приветствия и меню команд</i>
     * @param updates
     * @return
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message()!=null) {
                String messageText = null;
                LocalDateTime sendTime = LocalDateTime.now();
                LocalDateTime justNow = sendTime.truncatedTo(ChronoUnit.SECONDS);
                String content = update.message().text();
                long chatId = update.message().chat().id();
                loggerConsole.info("Processing Chat update:In chat \"{}\" From \"{}\" came message - \"{}\"",
                        update.message().chat().firstName()+" "+update.message().chat().lastName(),
                        update.message().from().firstName()+" "+update.message().from().lastName(),
                        content);
                if (parseNotificationTask(content).equals("/start")) {
                    messageText = INTRO_INFO;
                    dasNeedToTreateNotifies = true;
                }
                if (messageText != null){
                    SendMessage messageToChat = new SendMessage(chatId, messageText);
                    SendResponse responseFromBot = telegramBot.execute(messageToChat);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
