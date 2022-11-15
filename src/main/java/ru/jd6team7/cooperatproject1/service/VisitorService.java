package ru.jd6team7.cooperatproject1.service;

import liquibase.sqlgenerator.core.GetViewDefinitionGeneratorInformix;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.repository.VisitorRepository;

@Service
public class VisitorService {

    VisitorRepository visitorRepository;

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public Visitor addVisitor(long chatId) {
        Visitor visitor = new Visitor(chatId);
        visitor.setMessageStatus(Visitor.MessageStatus.BASE);
        visitorRepository.save(visitor);
        return visitor;
    }

    public Visitor findVisitor(long chatId) {
        return visitorRepository.findByChatId(chatId).orElse(null);
    }

    public Visitor updateVisitor(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    public void updateMessageStatus(long chatId, Visitor.MessageStatus messageStatus) {
        visitorRepository.updateMessageStatus(chatId, messageStatus);
    }
}
