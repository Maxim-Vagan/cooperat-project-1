package ru.jd6team7.cooperatproject1.distributor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.sender.catSender.BaseCatSender;
import ru.jd6team7.cooperatproject1.sender.catSender.CatInfoPetSender;
import ru.jd6team7.cooperatproject1.sender.catSender.InfoCatShelterSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatDistributorTest {

    @Mock
    private BaseCatSender baseCatSender;
    @Mock
    private InfoCatShelterSender infoCatShelterSender;
    @Mock
    private CatInfoPetSender catInfoPetSender;
    @Mock
    private VolunteerSender volunteerSender;
    @Mock
    private VisitorService visitorService;

    @InjectMocks
    private CatDistributor d;

    private Visitor visitor;
    @BeforeEach
    private void init() {
        visitor = new Visitor(1L);
        visitor.setMessageStatus(Visitor.MessageStatus.BASE);
    }

    @Test
    void getDistributeTest() {
        long chatId = 1;
        String message = "/info";
        when(visitorService.findVisitor(chatId)).thenReturn(visitor);
        d.getDistribute(chatId, message);
        verify(infoCatShelterSender).sendIntro(chatId);
        message = "/help";
        d.getDistribute(chatId, message);
        verify(volunteerSender).sendIntro(chatId);
        message = "/back";
        d.getDistribute(chatId, message);
        verify(baseCatSender).sendIntro(chatId);
        message = "/cat";
        d.getDistribute(chatId, message);
        message = "/takePet";
        d.getDistribute(chatId, message);
        verify(catInfoPetSender).sendIntro(chatId);
        message = "";
        d.getDistribute(chatId, message);
        verify(baseCatSender).process(chatId, message);
        visitor.setMessageStatus(Visitor.MessageStatus.SHELTER_INFO);
        d.getDistribute(chatId, message);
        verify(infoCatShelterSender).process(chatId, message);
        visitor.setMessageStatus(Visitor.MessageStatus.PET_INFO);
        d.getDistribute(chatId, message);
        verify(catInfoPetSender).process(chatId, message);
        visitor.setMessageStatus(Visitor.MessageStatus.GET_CALLBACK);
        d.getDistribute(chatId, message);
        verify(volunteerSender).process(chatId, message);
    }

}