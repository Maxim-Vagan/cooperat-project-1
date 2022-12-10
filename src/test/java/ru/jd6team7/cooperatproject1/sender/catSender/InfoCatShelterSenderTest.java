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
import ru.jd6team7.cooperatproject1.sender.dogSender.InfoDogShelterSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InfoCatShelterSenderTest {

    @Mock
    TelegramBot tgb;
    @Mock
    VisitorService visitorService;

    @Spy
    @InjectMocks
    InfoCatShelterSender iss;

    private final String INTRO_INFO = "Я с радостью поделюсь с вами информации о нас. \r\n" +
            "* Общая информация о приюте ( /infoAboutUs)\r\n" +
            "* Расписание работы, адрес, схема проезда ( /address)\r\n" +
            "* Техника безопасности ( /safety)\r\n" +
            "* Позвать на помощь ( /help)\r\n" +
            "* Для возврата назад: ( /back)";

    private final String ABOUT_US = "Мы - Кошачий рай. Наша цель - чтобы любая бездомная душа нашла своих любящих хозяев. Кошачий рай. Рай там, где мы!";
    private final String ADDRESS_AND_SCHEDULE = "Мы находимся по адресу Лебедева 35. Работаем круглочуточно, без выходных";
    private final String ABOUT_SAFETY = "Кошек не кормить! Пальцы в клетку не совать! Персонал не дразнить!";
    private final long chatId = 1;

    @ParameterizedTest
    @ValueSource(strings = {"/infoAboutUs", "/address", "/safety", ""})
    void processTest(String message) {
        iss.process(chatId, message);
        if(message.equals("/infoAboutUs")) {
            Mockito.verify(iss).sendMessage(chatId, ABOUT_US);
        } else if(message.equals("/address")) {
            Mockito.verify(iss).sendMessage(chatId, ADDRESS_AND_SCHEDULE);
        } else if(message.equals("/safety")) {
            Mockito.verify(iss).sendMessage(chatId, ABOUT_SAFETY);
        } else {

            Mockito.verify(iss).getIncorrectRequest(chatId);
        }
    }

    @Test
    void sendIntroTest() {
        iss.sendIntro(chatId);
        Mockito.verify(visitorService).updateMessageStatus(chatId, Visitor.MessageStatus.SHELTER_INFO);
    }
}