package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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


@ExtendWith(MockitoExtension.class)
class VolunteerSenderTest {
    @Mock
    TelegramBot telegramBot;
    @Mock
    VisitorService visitorService;
    @Spy
    @InjectMocks
    VolunteerSender vs;
    private final String INTRO_WITHOUT_PHONE = "Пожалуйста, укажите номер телефона для связи. " +
            "Мы обязательно свяжемся с вами.";
    private final String DONE = "Ваш запрос получен. Мы свяжемся с вами в максимально короткие сроки";
    private final String NUMBER_NOT_FOUND = "Не удалось распознать номер телефона." +
            "Номер должен состоять из 10 или 11 цифр в любом формате. Для возврата назад: (/back)";
    private static Visitor visitor;


    @BeforeAll
    static void init() {
        visitor = new Visitor(1);
    }
    private final long chatId = 1;

    @Test
    void sendIntroTest() {
        Mockito.when(visitorService.findVisitor(chatId)).thenReturn(visitor);
        visitor.setPhoneNumber(null);
        vs.sendIntro(chatId);
        Mockito.verify(vs).sendMessage(chatId, INTRO_WITHOUT_PHONE);
        visitor.setPhoneNumber("1");
        vs.sendIntro(chatId);
        Assertions.assertTrue(visitor.isNeedCallback());
        Mockito.verify(vs).sendMessage(chatId, DONE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"9999999999", "99999999999"})
    void processPositiveTest(String message) {
        Mockito.when(visitorService.findVisitor(chatId)).thenReturn(visitor);
        vs.process(chatId, message);
        Assertions.assertTrue(visitor.isNeedCallback());
        Mockito.verify(vs).sendMessage(chatId, DONE);
        /*Mockito.verify(sender).sendIntro(chatId);*/ //Как это протестить можно? Мок не видит связь на родителя. Отдельный мок сендера тоже не помогает.
    }

    @ParameterizedTest
    @ValueSource(strings = {"99", ""})
    void processNegativeTest(String message) {
        Mockito.when(visitorService.findVisitor(chatId)).thenReturn(visitor);
        vs.process(chatId, message);
        Mockito.verify(vs).sendMessage(chatId, NUMBER_NOT_FOUND);
    }

}