package ru.jd6team7.cooperatproject1.service;

import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.repository.VisitorRepository;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    //Используется только в листенере, когда пишет новый юзер. Сохраняет только chatID. Все остальное делать через перегруз
    public Visitor addVisitor(Long chatId) {
        Visitor visitor = new Visitor(chatId);
        return addVisitor(visitor);
    }

    public Visitor addVisitor(Visitor visitor) {
        visitor.setMessageStatus(Visitor.MessageStatus.BASE);
        return visitorRepository.save(visitor);
    }

    public Visitor findVisitor(long chatId) {
        return visitorRepository.findByChatId(chatId).orElse(null);
    }

    public Visitor updateVisitor(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    public void deleteVisitor(Visitor visitor) {
        visitorRepository.delete(visitor);
    }

    public void updateMessageStatus(long chatId, Visitor.MessageStatus messageStatus) {
        visitorRepository.updateMessageStatus(chatId, messageStatus);
    }

    public void updateShelterStatus(long chatId, Visitor.ShelterStatus shelterStatus) {
        visitorRepository.updateShelterStatus(chatId, shelterStatus);
    }
}
