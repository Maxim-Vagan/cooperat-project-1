package ru.jd6team7.cooperatproject1.sender.dogSender;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DogInfoPetSenderTest {

    @Mock
    TelegramBot tgb;
    @Mock
    VisitorService visitorService;

    @Spy
    @InjectMocks
    DogInfoPetSender dips;

    String INTRO_INFO = "Вы можете получить следующую информацию о животных: \r\n" +
            "* Правила знакомства с животным ( /aboutFirstVisit)\r\n" +
            "* Список документов для удочерения ( /documents)\r\n" +
            "* Рекомендации по транспортировке ( /travel)\r\n" +
            "* Рекомендации по обустройству дома для животного ( /service)\r\n" +
            "* Возможнные причины отказа в удочерении ( /rejectionReason)\r\n" +
            "* Первичные советы кинолога и рекомендации, к кому обратиться ( /kinolog)\r\n" +
            "* Позвать на помощь ( /help)\r\n" +
            "* Для возврата назад: ( /back)";

    private final String FIRST_VISIT = "Поздоровайтесь, проявляя уважение. Не кормите, погладьте, спойте. Он будет рад";
    private final String DOCUMENTS = "Паспорт и хорошее настроение";
    private final String TRAVEL = "Возьмите поводок и намордник. На первое время это нужно. Добираться до дома лучше всего на машине";
    private final String SERVICE = "Животное следует обеспечить домиком, когтеточилкой и игрушками в неограниченном количестве";
    private final String REJECTION_REASON = "Вам не отдадут собаку, если вы идиот или кореец";
    private final String KINOLOG = "Обращаться нежно, дрессировать после месяца проживания. Кормить, любить, никогда не обижать. Обращаться туда то, он хороший";
    private final long chatId = 1;

    @Test
    void sendIntro() {
        dips.sendIntro(chatId);
        Mockito.verify(visitorService).updateMessageStatus(chatId, Visitor.MessageStatus.PET_INFO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/aboutFirstVisit", "/documents", "/travel","/service", "/rejectionReason", "/kinolog", ""})
    void processTest(String message) {
        dips.process(chatId, message);
        switch (message) {
            case "/aboutFirstVisit" -> Mockito.verify(dips).sendMessage(chatId, FIRST_VISIT);
            case "/documents" -> Mockito.verify(dips).sendMessage(chatId, DOCUMENTS);
            case "/travel" -> Mockito.verify(dips).sendMessage(chatId, TRAVEL);
            case "/service" -> Mockito.verify(dips).sendMessage(chatId, SERVICE);
            case "/rejectionReason" -> Mockito.verify(dips).sendMessage(chatId, REJECTION_REASON);
            case "/kinolog" -> Mockito.verify(dips).sendMessage(chatId, KINOLOG);
            default -> Mockito.verify(dips).getIncorrectRequest(chatId);
        }
    }
}