package ru.jd6team7.cooperatproject1.distributor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.dogSender.BaseDogSender;
import ru.jd6team7.cooperatproject1.sender.dogSender.InfoDogShelterSender;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogDistributorTest {
    @Mock
    private BaseDogSender baseDogSender;
    @Mock
    private InfoDogShelterSender infoDogShelterSender;
    @Mock
    private VolunteerSender volunteerSender;
    @Mock
    private VisitorService visitorService;

    @InjectMocks
    private DogDistributor d;

    private Visitor visitor;
    @BeforeEach
    private void init() {
        visitor = new Visitor(1);
        visitor.setMessageStatus(Visitor.MessageStatus.BASE);
    }

    @Test
    void getDistributeTest() {
        long chatId = 1;
        String message = "/info";
        when(visitorService.findVisitor(chatId)).thenReturn(visitor);
        d.getDistribute(chatId, message);
        verify(infoDogShelterSender).sendIntro(chatId);
        message = "/help";
        d.getDistribute(chatId, message);
        verify(volunteerSender).sendIntro(chatId);
        message = "/back";
        d.getDistribute(chatId, message);
        verify(baseDogSender).sendIntro(chatId);
        message = "/dog";
        d.getDistribute(chatId, message);
        verify(baseDogSender).sendIntro(chatId);
        message = "";
        d.getDistribute(chatId, message);
        verify(baseDogSender).process(chatId, message);
        visitor.setMessageStatus(Visitor.MessageStatus.SHELTER_INFO);
        d.getDistribute(chatId, message);
        verify(infoDogShelterSender).process(chatId, message);
        visitor.setMessageStatus(Visitor.MessageStatus.GET_CALLBACK);
        d.getDistribute(chatId, message);
        verify(volunteerSender).process(chatId, message);
    }


}