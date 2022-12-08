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
import ru.jd6team7.cooperatproject1.sender.Sender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BaseDogSenderTest {

    @Mock
    TelegramBot tgb;
    @Mock
    VisitorService visitorService;

    @Spy
    @InjectMocks
    BaseDogSender bds;

    private final String INTRO_INFO = "Собачий рай приветствует Вас.\r\n" +
            "- Выбрать другой приют ( /anotherShelter)\r\n" +
            "- Узнать информацию о приюте ( Этап 1 /info)\r\n" +
            "- Как взять питомца из приюта ( Этап-2 /takePet)\r\n" +
            "- Прислать отчет о питомце ( Этап-3 /sendReport)\r\n" +
            "- Позвать волонтёра ( /help)";
    private final long chatId = 1;
    @Test
    void sendIntro() {
        bds.sendIntro(chatId);
        Mockito.verify(visitorService).updateMessageStatus(chatId, Visitor.MessageStatus.BASE);
    }

}