package ru.jd6team7.cooperatproject1.service;

import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.repository.VisitorRepository;

@Service
public class VisitorService {

    VisitorRepository visitorRepository;

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    private Visitor addVisitor(String chatId)
}
