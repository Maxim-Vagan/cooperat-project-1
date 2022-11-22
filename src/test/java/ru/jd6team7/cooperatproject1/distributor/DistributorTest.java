package ru.jd6team7.cooperatproject1.distributor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.sender.BaseSender;
import ru.jd6team7.cooperatproject1.sender.InfoShelterSender;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistributorTest {
    @Mock
    private BaseSender baseSender;
    @Mock
    private InfoShelterSender infoShelterSender;
    @Mock
    private VolunteerSender volunteerSender;
    @Mock
    private VisitorService visitorService;

    @InjectMocks
    private Distributor d;

    private Visitor visitor;
    @BeforeEach
    private void init() {
        visitor = new Visitor(1L);
        visitor.setMessageStatus(Visitor.MessageStatus.BASE);
    }

    //А есть способ это через параметризированный нормально написать? У меня еще говнокодистее получилось, чем так:(
    @Test
    void getDistributeTest() {
        long chatId = 1;
        String message = "/info";
        when(visitorService.findVisitor(chatId)).thenReturn(visitor);
        d.getDistribute(chatId, message);
        verify(infoShelterSender).sendIntro(chatId);
        message = "/help";
        d.getDistribute(chatId, message);
        verify(volunteerSender).sendIntro(chatId);
        message = "/back";
        d.getDistribute(chatId, message);
        verify(baseSender).sendIntro(chatId);
        message = "/start";
        d.getDistribute(chatId, message);
        verify(baseSender).sayHelloAfterStart(chatId);
        message = "";
        d.getDistribute(chatId, message);
        verify(baseSender).process(chatId, message);
        visitor.setMessageStatus(Visitor.MessageStatus.SHELTER_INFO);
        d.getDistribute(chatId, message);
        verify(infoShelterSender).process(chatId, message);
        visitor.setMessageStatus(Visitor.MessageStatus.GET_CALLBACK);
        d.getDistribute(chatId, message);
        verify(volunteerSender).process(chatId, message);
    }


}