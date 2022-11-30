package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import javax.activation.MimetypesFileTypeMap;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Сендер на callback. Вызывается из любого класса, дистрибьютер перехватывает сообщение /help на любом этапе
 * Ставит отметку в БД о необходимости перезвонить и собирает номер телефона
 */
@Component
public class DailyReportSender extends Sender {
    @Value("${pets.photos.dir.path}")
    private String DAILY_PHOTOS_DIR;
    private final TryPeriodRepository tpRepo;
    private final Visitor.MessageStatus status = Visitor.MessageStatus.SEND_DAILY_REPORT;
    private final String INTRO_INFO_DR = """
            Уважаемый Опекун! Для успешной сдачи отчёта
            пришлите фото питомца с подписью следующего формата (Пример):
            *_"питомец: Барбос_*
            *_Самочувствие хорошее. Настроение спокойное. Дневной рацион: сухой корм,_*
            *_влажный корм - раз в сутки. Из новых привычек - привыкает к лежанке"_*
            
             - Позвать волонтёра: (/help)
             - Для возврата назад: (/back)
            """;
    private final String NO_REPORT_TEXT = "В присланном отчете отсутствует текст!";
    private final String NO_REPORT_PHOTO = "В присланном отчете отсутствует фото!";
    private final String UNRECOGNIZED_PET_NAME = "В присланном отчете отсутствует имя Питомца!";
    private final String PET_NOT_RECOGNIZED = "Пожалуйста, пришлите отчёт в указанном формате.\r\n" +
            "Позвать волонтёра: (/help)\r\nДля возврата назад: (/back)";
    private final String REPORT_WITHOUT_TEXT_INFO = "Пожалуйста, внесите текстовую информацию по отчёту.\r\n" +
            "Позвать волонтёра: (/help)\r\nДля возврата назад: (/back)";
    private final String REPORT_WITHOUT_PHOTO_INFO = "Пожалуйста, прикрепите фото к отчёту.\r\n" +
            "Позвать волонтёра: (/help)\r\nДля возврата назад: (/back)";
    private final String DONE = "Ваш отчёт сохранён. Для возврата назад: (/back)";
    private long curChatId = 0L;

    public DailyReportSender(VisitorService visitorService, TelegramBot telegramBot, TryPeriodRepository tpRepo) {
        super(visitorService, telegramBot);
        this.tpRepo = tpRepo;
    }

    /** Интро для ознакомления Опекуна с форматом заполнения
     *  присылаемого Ежедневного отчёта */
    @Override
    public void sendIntro(long chatId) {
        visitorService.updateMessageStatus(chatId, status);
        this.curChatId = chatId;
        sendMessage(chatId, INTRO_INFO_DR);
    }

    /** Метод вытаскивает благодаря рег. выражениям имя Питомца и Текст отчета.
     * @return Строка с именем и текстом */
    private String[] parsePetNameAndDailyText(String caption){
        String[] parseResult = {"", ""};
//        "питомец:\\s*(\\w+?)\\s*"
//        "питомец:\\s*\\w+?\\s*\\n(.*)"
        Matcher m1 = Pattern.compile("питомец:",
                        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
                .matcher(caption);
        Matcher m2 = Pattern.compile("питомец:",
                        Pattern.CASE_INSENSITIVE)
                .matcher(caption);
        if (m1.matches()) { parseResult[0] = m1.group(); }
        if (m2.matches()) { parseResult[1] = m2.group(); }
        if (Pattern.matches("питомец:", "питомец: Лили\nЛюбопытна")){
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
        return parseResult;
    }

    /** Метод основной обработки сообщений от Юзера.
     * Обработка поведения, когда отчёт без Фото или текста */
    @Override
    public void process(long chatId, String message) {
        Visitor visitor = visitorService.findVisitor(chatId);
    }

    public void processUpdate(long chatId, Update update) {
        Visitor visitor = visitorService.findVisitor(chatId);
        String petInfo = "";
        String[] petNameAndText = {"", ""};
        if (update.message().text() != null && update.message().caption() == null){
            petNameAndText = parsePetNameAndDailyText(update.message().text());
            petInfo = tpRepo.getPetInfoForDailyReport(petNameAndText[0], chatId);
        } else if (update.message().text() == null && update.message().caption() != null) {
            petNameAndText = parsePetNameAndDailyText(update.message().caption());
            petInfo = tpRepo.getPetInfoForDailyReport(petNameAndText[0], chatId);
        } else if (update.message().text() == null && update.message().caption() == null) {
            sendMessage(chatId, NO_REPORT_TEXT);
        }
        if (petInfo == null) {
            sendMessage(chatId, NO_REPORT_TEXT);
        }
        if (update.message().photo() != null){
            int photoCount = update.message().photo().length - 1;
            PhotoSize photoImage = update.message().photo()[photoCount];

            File filePhoto = telegramBot.execute(new GetFile(photoImage.fileId())).file();
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            String fileExtension = mimeTypesMap.getContentType(String.valueOf(filePhoto));
            String dailyPhotoFileName = petInfo.split(";")[2].replace("$", fileExtension.split("/")[1]);
            try (FileInputStream fis = new FileInputStream(String.valueOf(filePhoto));
                 FileOutputStream fos = new FileOutputStream(DAILY_PHOTOS_DIR + "/for_daily_reports/");
            ) {fis.transferTo(fos);} catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sendMessage(chatId, NO_REPORT_PHOTO);
        }
    }
}

