package ru.jd6team7.cooperatproject1.sender.catSender;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.Sender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/** Обработка всех поступивших сообщений от пользователя со статусом PET_INFO
 */
@Component
public class CatInfoPetSender extends Sender {
    private final Visitor.MessageStatus status = Visitor.MessageStatus.PET_INFO;
    private final String INTRO_INFO = "Вы можете получить следующую информацию о животных: \r\n" +
            "* Правила знакомства с животным ( /aboutFirstVisit)\r\n" +
            "* Список документов для удочерения ( /documents)\r\n" +
            "* Рекомендации по транспортировке ( /travel)\r\n" +
            "* Рекомендации по обустройству дома для животного ( /service)\r\n" +
            "* Возможнные причины отказа в удочерении ( /rejectionReason)\r\n" +
            "* Позвать на помощь ( /help)\r\n" +
            "* Для возврата назад: ( /back)";

    private final String FIRST_VISIT = "Поздоровайтесь, проявляя уважение. Не кормите, погладьте, спойте. Он будет рад";
    private final String DOCUMENTS = "Паспорт и хорошее настроение";
    private final String TRAVEL = "Перевозить животное следует в переноске. Ему не понравится, но он привыкнет";
    private final String SERVICE = "Животное следует обеспечить домиком, когтеточилкой и игрушками в неограниченном количестве";
    private final String REJECTION_REASON = "Вам не отдадут котенка, если вы идиот или кореец";

    protected CatInfoPetSender(VisitorService visitorService, TelegramBot telegramBot) {
        super(visitorService, telegramBot);
    }

    @Override
    public void sendIntro(long chatId) {
        visitorService.updateMessageStatus(chatId, status);
        sendMessage(chatId, INTRO_INFO);
    }

    @Override
    public void process(long chatId, String message) {
        switch (message) {
            case "/aboutFirstVisit" -> sendMessage(chatId, FIRST_VISIT);
            case "/documents" -> sendMessage(chatId, DOCUMENTS);
            case "/travel" -> sendMessage(chatId, TRAVEL);
            case "/service" -> sendMessage(chatId, SERVICE);
            case "/rejectionReason" -> sendMessage(chatId, REJECTION_REASON);
            default -> getIncorrectRequest(chatId);
        }
    }
}
