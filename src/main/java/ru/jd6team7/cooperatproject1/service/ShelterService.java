package ru.jd6team7.cooperatproject1.service;

import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.repository.ShelterRepository;

@Service
public class ShelterService {

    ShelterRepository shelterRepository;
    VisitorService visitorService;
    public ShelterService(ShelterRepository shelterRepository, VisitorService visitorService) {
        this.shelterRepository = shelterRepository;
        this.visitorService = visitorService;
    }

}
