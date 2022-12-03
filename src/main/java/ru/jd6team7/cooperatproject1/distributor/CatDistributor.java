package ru.jd6team7.cooperatproject1.distributor;

import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.sender.catSender.BaseCatSender;
import ru.jd6team7.cooperatproject1.sender.catSender.InfoCatShelterSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/**Распределялка по кошачим сендерам. Сообщения с базовым статусом обработаны отдельно
 * Сообщения с другими статусами обрабатываются в нужном сендере
 */
@Component
public class CatDistributor extends Distributor{
    private final BaseCatSender baseCatSender;
    private final InfoCatShelterSender infoCatShelterSender;
    private final VolunteerSender volunteerSender;
    private final VisitorService visitorService;

    public CatDistributor(BaseCatSender baseCatSender, InfoCatShelterSender infoCatShelterSender, VolunteerSender volunteerSender, VisitorService visitorService) {
        this.baseCatSender = baseCatSender;
        this.infoCatShelterSender = infoCatShelterSender;
        this.volunteerSender = volunteerSender;
        this.visitorService = visitorService;
    }

    @Override
    public void getDistribute(long chatId, String message) {
        Visitor.MessageStatus status = visitorService.findVisitor(chatId).getMessageStatus();
        switch (message) {
            case "/info" -> infoCatShelterSender.sendIntro(chatId);
            case "/help" -> volunteerSender.sendIntro(chatId);
            case "/back", "/dog" -> baseCatSender.sendIntro(chatId);
            default -> {
                switch (status) {
                    case BASE -> baseCatSender.process(chatId, message);
                    case SHELTER_INFO -> infoCatShelterSender.process(chatId, message);
                    case GET_CALLBACK -> volunteerSender.process(chatId, message);

                }
            }
        }
    }
}
