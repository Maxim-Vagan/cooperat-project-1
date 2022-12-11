package ru.jd6team7.cooperatproject1.sender.dogSender;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.Sender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/** Обработка всех поступивших сообщений от пользователя со статусом PET_INFO
 */
@Component
public class DogInfoPetSender extends Sender {

    protected DogInfoPetSender(VisitorService visitorService, TelegramBot telegramBot) {
        super(visitorService, telegramBot);
    }
    private final Visitor.MessageStatus status = Visitor.MessageStatus.PET_INFO;
    private final String INTRO_INFO = "Вы можете получить следующую информацию о животных: \r\n" +
            "* Правила знакомства с животным ( /aboutFirstVisit)\r\n" +
            "* Список документов для удочерения ( /documents)\r\n" +
            "* Рекомендации по транспортировке ( /travel)\r\n" +
            "* Рекомендации по обустройству дома для животного ( /service)\r\n" +
            "* Возможнные причины отказа в удочерении ( /rejectionReason)\r\n" +
            "* Первичные советы кинолога и рекомендации, к кому обратиться ( /kinolog)\r\n" +
            "* Позвать на помощь ( /help)\r\n" +
            "* Для возврата назад: ( /back)";

    private final String FIRST_VISIT = "Поздоровайтесь, проявляя уважение. Не кормите, погладьте, спойте. Он будет рад";
    private final String DOCUMENTS = "Паспорт и хорошее настроение";
    private final String TRAVEL = "Возьмите поводок и намордник. На первое время это нужно. Добираться до дома лучше всего на машине";
    private final String SERVICE = "Животное следует обеспечить домиком, когтеточилкой и игрушками в неограниченном количестве";
    private final String REJECTION_REASON = "Вам не отдадут собаку, если вы идиот или кореец";
    private final String KINOLOG = "Обращаться нежно, дрессировать после месяца проживания. Кормить, любить, никогда не обижать. Обращаться туда то, он хороший";

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
            case "/kinolog" -> sendMessage(chatId, KINOLOG);
            default -> getIncorrectRequest(chatId);
        }
    }
}
