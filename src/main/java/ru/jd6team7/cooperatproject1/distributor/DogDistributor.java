package ru.jd6team7.cooperatproject1.distributor;

import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.dogSender.BaseDogSender;
import ru.jd6team7.cooperatproject1.sender.dogSender.InfoDogShelterSender;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/**Распределялка по собачим сендерам. Сообщения с базовым статусом обработаны отдельно
 * Сообщения с другими статусами обрабатываются в нужном сендере
 */
@Component
public class DogDistributor extends Distributor{
    private final BaseDogSender baseDogSender;
    private final InfoDogShelterSender infoDogShelterSender;
    private final VolunteerSender volunteerSender;
    private final VisitorService visitorService;

    public DogDistributor(BaseDogSender baseDogSender, InfoDogShelterSender infoDogShelterSender, VolunteerSender volunteerSender, VisitorService visitorService) {
        this.baseDogSender = baseDogSender;
        this.infoDogShelterSender = infoDogShelterSender;
        this.volunteerSender = volunteerSender;
        this.visitorService = visitorService;
    }

    @Override
    public void getDistribute(long chatId, String message) {
        Visitor.MessageStatus status = visitorService.findVisitor(chatId).getMessageStatus();
        switch (message) {
            case "/info" -> infoDogShelterSender.sendIntro(chatId);
            case "/help" -> volunteerSender.sendIntro(chatId);
            case "/back", "/dog" -> baseDogSender.sendIntro(chatId);
            default -> {
                switch (status) {
                    case BASE -> baseDogSender.process(chatId, message);
                    case SHELTER_INFO -> infoDogShelterSender.process(chatId, message);
                    case GET_CALLBACK -> volunteerSender.process(chatId, message);
                }
            }
        }
    }
}
