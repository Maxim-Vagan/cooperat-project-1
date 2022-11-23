package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/** Родитель для сендеров. Каждый сендер отвечает за свой статус сообщений у юзера.
 * Здесь базовые методы для отправки, некорректного запроса и intro, переводящий статус на BASE
 */
@Component
public abstract class Sender {
    protected final VisitorService visitorService;
    protected final TelegramBot telegramBot;
    private Visitor.MessageStatus status = Visitor.MessageStatus.BASE;
    private String INTRO_INFO = "* Узнать информацию о приюте ( Этап 1 /info)\r\n" +
            "* Как взять питомца из приюта ( Этап-2 /takePet)\r\n" +
            "* Прислать отчет о питомце ( Этап-3 /sendReport)\r\n" +
            "* Позвать волонтёра ( /help)";
    private final String INCORRECT_REQUEST = "Некорректный запрос. Повторите попытку. Для возврата назад: (/back)";

    protected Sender(VisitorService visitorService, TelegramBot telegramBot) {
        this.visitorService = visitorService;
        this.telegramBot = telegramBot;
    }
    public void sendIntro(long chatId) {
        visitorService.updateMessageStatus(chatId, status);
        sendMessage(chatId, INTRO_INFO);
    }
    public abstract void process(long chatId, String message);
    public void sendMessage(long chatId, String message) {
        telegramBot.execute(new SendMessage(chatId, message));
    }
    public void getIncorrectRequest(long chatId) {
        sendMessage(chatId, INCORRECT_REQUEST);
    }
}
