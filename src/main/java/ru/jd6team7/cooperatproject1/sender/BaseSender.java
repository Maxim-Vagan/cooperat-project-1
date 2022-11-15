package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/** Выводит команду особое приветствие на команду /start
 * Обрабатывает сообщений со статусом BASE, сейчас - все 3 возможных варианта обработаны в Дестрибьюторе
 * Все остальное выдает пользователю ошибку
 */

@Component
public class BaseSender extends Sender{
    private final Visitor.MessageStatus status = Visitor.MessageStatus.BASE;
    private final String INTRO_INFO = "* Узнать информацию о приюте ( Этап 1 /info)\r\n" +
            "* Как взять питомца из приюта ( Этап-2 /takePet)\r\n" +
            "* Прислать отчет о питомце ( Этап-3 /sendReport)\r\n" +
            "* Позвать волонтёра ( /help)";
    private final String START_MESSAGE = "Здравствуйте. Я бот-ассистент приюта для животных. Сделан, чтобы творить добро." +
            "* Узнать информацию о приюте (Этап 1 /info)\r\n" +
            "* Как взять питомца из приюта (Этап-2 /takePet)\r\n" +
            "* Прислать отчет о питомце (Этап-3 /sendReport)\r\n" +
            "* Позвать волонтёра (/help)";

    public BaseSender(VisitorService visitorService, TelegramBot telegramBot) {
        super(visitorService, telegramBot);
    }

    @Override
    public void sendIntro(long chatId) {
        visitorService.updateMessageStatus(chatId, status);
        sendMessage(chatId, INTRO_INFO);
    }

    /** Все, что дошло до сюда - некорректный запрос.
     * Корректные перехватываются в Дистрибьюторе. Три возможных запроса со статусом BASE обработаны.
     */
    @Override
    public void process(long chatId, String message) {
        super.getIncorrectRequest(chatId);
    }
    public void sayHelloAfterStart(long chatId) {
        sendMessage(chatId, START_MESSAGE);
    }
}
