package ru.jd6team7.cooperatproject1.sender.catSender;

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
import ru.jd6team7.cooperatproject1.sender.dogSender.DogInfoPetSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CatInfoPetSenderTest {

    @Mock
    TelegramBot tgb;
    @Mock
    VisitorService visitorService;

    @Spy
    @InjectMocks
    CatInfoPetSender cips;
    private final String INTRO_INFO = "Вы можете получить следующую информацию о животных: \r\n" +
            "* Правила знакомства с животным ( /aboutFirstVisit)\r\n" +
            "* Список документов для удочерения ( /documents)\r\n" +
            "* Рекомендации по транспортировке ( /travel)\r\n" +
            "* Рекомендации по обустройству дома для животного ( /service)\r\n" +
            "* Возможнные причины отказа в удочерении ( /rejectionReason)\r\n" +
            "* Позвать на помощь ( /help)\r\n" +
            "* Для возврата назад: ( /back)";

    private final String FIRST_VISIT = "Поздоровайтесь, проявляя уважение. Не кормите, погладьте, спойте. Он будет рад";
    private final String DOCUMENTS = "Паспорт и хорошее настроение";
    private final String TRAVEL = "Перевозить животное следует в переноске. Ему не понравится, но он привыкнет";
    private final String SERVICE = "Животное следует обеспечить домиком, когтеточилкой и игрушками в неограниченном количестве";
    private final String REJECTION_REASON = "Вам не отдадут котенка, если вы идиот или кореец";
    private final long chatId = 1;

    @Test
    void sendIntro() {
        cips.sendIntro(chatId);
        Mockito.verify(visitorService).updateMessageStatus(chatId, Visitor.MessageStatus.PET_INFO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/aboutFirstVisit", "/documents", "/travel","/service", "/rejectionReason", ""})
    void processTest(String message) {
        cips.process(chatId, message);
        switch (message) {
            case "/aboutFirstVisit" -> Mockito.verify(cips).sendMessage(chatId, FIRST_VISIT);
            case "/documents" -> Mockito.verify(cips).sendMessage(chatId, DOCUMENTS);
            case "/travel" -> Mockito.verify(cips).sendMessage(chatId, TRAVEL);
            case "/service" -> Mockito.verify(cips).sendMessage(chatId, SERVICE);
            case "/rejectionReason" -> Mockito.verify(cips).sendMessage(chatId, REJECTION_REASON);
            default -> Mockito.verify(cips).getIncorrectRequest(chatId);
        }
    }
}