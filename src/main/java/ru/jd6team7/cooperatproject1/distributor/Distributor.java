package ru.jd6team7.cooperatproject1.distributor;

import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.sender.BaseSender;
import ru.jd6team7.cooperatproject1.sender.InfoShelterSender;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/**Распределялка по сендерам. Сообщения с базовым статусом обработаны отдельно
 * Сообщения с другими статусами обрабатываются в нужном сендере
 */
@Component
public class Distributor {
    private final BaseSender baseSender;
    private final InfoShelterSender infoShelterSender;
    private final VolunteerSender volunteerSender;
    private final VisitorService visitorService;

    public Distributor(BaseSender baseSender, InfoShelterSender infoShelterSender, VolunteerSender volunteerSender, VisitorService visitorService) {
        this.baseSender = baseSender;
        this.infoShelterSender = infoShelterSender;
        this.volunteerSender = volunteerSender;
        this.visitorService = visitorService;
    }

    public void getDistribute(long chatId, String message) {
        Visitor.MessageStatus status = visitorService.findVisitor(chatId).getMessageStatus();
        switch (message) {
            case "/info" -> infoShelterSender.sendIntro(chatId);
            case "/help" -> volunteerSender.sendIntro(chatId);
            case "/back" -> baseSender.sendIntro(chatId);
            case "/start" -> baseSender.sayHelloAfterStart(chatId);
            default -> {
                switch (status) {
                    //Все сообщения с BASE обработаны выше. Любое другое - некорректное.
                    case BASE -> baseSender.process(chatId, message);
                    case SHELTER_INFO -> infoShelterSender.process(chatId, message);
                    case GET_CALLBACK -> volunteerSender.process(chatId, message);

                }
            }
        }
    }
}
