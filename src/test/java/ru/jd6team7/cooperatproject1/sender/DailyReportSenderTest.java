package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;
import ru.jd6team7.cooperatproject1.sender.dogSender.InfoDogShelterSender;
import ru.jd6team7.cooperatproject1.service.DailyReportService;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyReportSenderTest {

    @Value("${pets.photos.dir.path}")
    private String DAILY_PHOTOS_DIR;
    @Mock
    private TryPeriodRepository tpRepo;
    @Mock
    private TelegramBot tgb;
    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private VisitorService visitorService;
    @Mock
    private VolunteerSender volunteerSender;
    @Mock
    private DailyReportService dailyReportService;
    private long chatId = 10L;

    @Spy
    @InjectMocks
    private DailyReportSender drs;

    private final Visitor.MessageStatus status = Visitor.MessageStatus.SEND_DAILY_REPORT;

    private final String INTRO_INFO_DR = """
            Уважаемый Опекун! Для успешной сдачи отчёта
            пришлите фото питомца с подписью следующего формата (Пример):
            *"питомец: Барбос*
            *Самочувствие хорошее. Настроение спокойное. Дневной рацион: сухой корм,*
            *влажный корм - раз в сутки. Из новых привычек - привыкает к лежанке"*
            
             - Позвать волонтёра: (/help)
             - Для возврата назад: (/back)
            """;
    private final String NO_REPORT_TEXT = "В присланном отчете отсутствует текст!";
    private final String NO_REPORT_PHOTO = "В присланном отчете отсутствует фото!";
    private final String NO_PET_PHOTO = "Пришлите Фото питомца по кличке: %s";
    private final String UNRECOGNIZED_PET_NAME = """
            В присланном отчете отсутствует имя Питомца!
            Либо указанного питомца нет под опекой у Вас""";
    private final String PET_NOT_RECOGNIZED = """
            Пожалуйста, пришлите отчёт в указанном формате.
            Позвать волонтёра: (/help)
            Для возврата назад: (/back)""";
    private final String REPORT_WITHOUT_TEXT_INFO = """
            Пожалуйста, внесите текстовую информацию по отчёту.
            Позвать волонтёра: (/help)
            Для возврата назад: (/back)""";
    private final String REPORT_WITHOUT_PHOTO_INFO = """
            Пожалуйста, прикрепите фото к отчёту.
            Позвать волонтёра: (/help)
            Для возврата назад: (/back)""";
    private final String DONE = "Ваш отчёт сохранён. Для возврата назад: (/back)";
    private long curChatId = 0L;

    @Test
    void sendIntro() {
        drs.sendIntro(chatId);
        Mockito.verify(visitorService).updateMessageStatus(chatId, status);
        verify(drs).sendMessage(chatId, INTRO_INFO_DR);
    }

    @Test
    void processUpdateTest() {
        when(update.message()).thenReturn(message);
        when(update.message().caption()).thenReturn("");
        when(update.message().text()).thenReturn("/help");
        drs.processUpdate(chatId, update);
        verify(volunteerSender).sendIntro(chatId);
        when(update.message().text()).thenReturn("");
        drs.processUpdate(chatId, update);
        verify(drs).sendMessage(chatId, NO_REPORT_TEXT);
        String petName = "Б";
        when(update.message().text()).thenReturn("Питомец: Барбос. Состояние ок");
        when(tpRepo.getPetInfoForDailyReport(petName, chatId)).thenReturn(null);
        drs.processUpdate(chatId, update);
        verify(drs).sendMessage(chatId, UNRECOGNIZED_PET_NAME);
        when(tpRepo.getPetInfoForDailyReport(petName, chatId)).thenReturn("Б");
        when(update.message().photo()).thenReturn(null);
        drs.processUpdate(chatId, update);
        verify(drs).sendMessage(chatId, NO_REPORT_PHOTO);

    }

}