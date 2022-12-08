package ru.jd6team7.cooperatproject1.service;

import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.repository.ShelterRepository;

@Service
public class ShelterService {
    /** Объект репозитория для работы данными Приютов в БД */
    private final ShelterRepository shelterRepository;
    /** Объект сервиса для описания Логики работы с Посетителями */
    private final VisitorService visitorService;
    /** Конструктор */
    public ShelterService(ShelterRepository shelterRepository, VisitorService visitorService) {
        this.shelterRepository = shelterRepository;
        this.visitorService = visitorService;
    }

}
