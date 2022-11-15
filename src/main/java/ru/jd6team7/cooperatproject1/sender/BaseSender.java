package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/** Обработка всех поступивших сообщений от пользователя со статусом BASE
 */

@Component
public class BaseSender {
    protected final VisitorService visitorService;
    protected final TelegramBot telegramBot;
    private final Visitor.MessageStatus status = Visitor.MessageStatus.BASE;
    private String INTRO_INFO = "* Узнать информацию о приюте (Этап 1 /info)\r\n" +
            "* Как взять питомца из приюта (Этап-2 /takePet)\r\n" +
            "* Прислать отчет о питомце (Этап-3 /sendReport)\r\n" +
            "* Позвать волонтёра (/help)";

    public BaseSender(VisitorService visitorService, TelegramBot telegramBot) {
        this.visitorService = visitorService;
        this.telegramBot = telegramBot;
    }
    public void sendIntro(long chatId) {
        visitorService.updateMessageStatus(chatId, status);
        sendMessage(chatId, INTRO_INFO);
    }
    public void sendInfo(long chatId, String message) {
        if(message.equals("/info")) {
            visitorService.updateMessageStatus(chatId,Visitor.MessageStatus.SHELTER_INFO);
            sendIntro(chatId);
        } else if(message.equals("/start")) {

        }
    }
    public void sendMessage(long chatId, String message) {
        telegramBot.execute(new SendMessage(chatId, message));
    }
}
