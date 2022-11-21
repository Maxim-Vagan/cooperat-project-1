package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InfoShelterSenderTest {

    @Mock
    TelegramBot telegramBot;
    @Mock
    VisitorService visitorService;

    @Spy
    @InjectMocks
    InfoShelterSender iss;

    private final String ABOUT_US = "Мы - Собачий рай. Наша цель - чтобы любая бездомная душа нашла своих любящих хозяев. Собачий рай. Рай там, где мы!";
    private final String ADDRESS_AND_SCHEDULE = "Мы находимся по адресу Голованова 35. Работаем круглочуточно, без выходных";
    private final String ABOUT_SAFETY = "Собак не кормить! Пальцы в клетку не совать! Персонал не дразнить!";
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
}