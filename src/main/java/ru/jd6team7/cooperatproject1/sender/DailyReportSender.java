package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.repository.DailyReportRepository;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;
import ru.jd6team7.cooperatproject1.sender.dogSender.InfoDogShelterSender;
import ru.jd6team7.cooperatproject1.service.DailyReportService;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final VolunteerSender volunteerSender;
    private final DailyReportService dailyReportService;
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

    public DailyReportSender(VisitorService visitorService,
                             TelegramBot telegramBot,
                             TryPeriodRepository tpRepo,
                             VolunteerSender volunteerSender,
                             DailyReportService dailyReportService) {
        super(visitorService, telegramBot);
        this.tpRepo = tpRepo;
        this.volunteerSender = volunteerSender;
        this.dailyReportService = dailyReportService;
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
        Matcher m1 = Pattern.compile("[Пп]итомец:\\s*([а-яА-ЯйЙ\\w]+)",
                        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE).matcher(caption);
        Matcher m2 = Pattern.compile("[Пп]итомец:.*\\n(.*)").matcher(caption);
        if (m1.find()) { parseResult[0] = m1.group(1); }
        if (m2.find()) { parseResult[1] = m2.group(1); }
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
        String incomeText = String.join(" ", update.message().caption(), update.message().text()).replace("null", "").strip();
        switch (incomeText) {
            case "/help" -> volunteerSender.sendIntro(chatId);
            case "/back" -> {
                if (visitor.getShelterStatus().equals(Visitor.ShelterStatus.DOG)) { super.sendIntro(chatId); }
                else { super.sendIntro(chatId); }
            }
            default -> {
                String petDbInfo = "";
                String[] petNameAndText = {"", ""};
                if (incomeText.isEmpty()) {
                    sendMessage(chatId, NO_REPORT_TEXT);
                } else if (update.message().text() != null && update.message().text().contains("итомец:")){
                    incomeText = String.join(" ", update.message().text(),  update.message().caption()).replace("null", "").strip();
                }
                petNameAndText = parsePetNameAndDailyText(incomeText);
                petDbInfo = tpRepo.getPetInfoForDailyReport(petNameAndText[0], chatId);
                if (petDbInfo == null && incomeText.length() > 0) {
                    sendMessage(chatId, UNRECOGNIZED_PET_NAME);
                } else if (update.message().photo() != null){
                    int photoCount = update.message().photo().length - 1;
                    PhotoSize photoImage = update.message().photo()[photoCount];
                    try {
                        Files.createDirectories(Path.of(DAILY_PHOTOS_DIR + "/for_daily_reports/"));
                    } catch (IOException ioe){
                        ioe.printStackTrace();
                    }
                    File filePhoto = telegramBot.execute(new GetFile(photoImage.fileId())).file();
                    MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
                    String fileExtension = mimeTypesMap.getContentType(filePhoto.filePath());
                    String dailyPhotoFileName = "";
                    if (petDbInfo != null && !petDbInfo.isEmpty() ) {
                        dailyPhotoFileName = petDbInfo.split(";")[2].replace("$", fileExtension.split("/")[1]);
                    }
                    try (InputStream bis = new ByteArrayInputStream(telegramBot.getFileContent(filePhoto));
                         FileOutputStream fos = new FileOutputStream( DAILY_PHOTOS_DIR + "/for_daily_reports/" +dailyPhotoFileName);
                    ) {
                        bis.transferTo(fos);
                        if (petNameAndText[1].isEmpty()) { sendMessage(chatId, NO_REPORT_TEXT); }
                        else {

                            dailyReportService.createDailyReport(Long.decode(petDbInfo.split(";")[1]),
                                    Integer.decode(petDbInfo.split(";")[1]),
                                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                    filePhoto.fileSize().longValue(), fileExtension,
                                    telegramBot.getFileContent(filePhoto),
                                    dailyPhotoFileName, petNameAndText[1]
                            );
                            sendMessage(chatId, DONE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    } else if (petDbInfo != null) { sendMessage(chatId, String.format(NO_PET_PHOTO, petNameAndText[0])); }
                else { sendMessage(chatId, NO_REPORT_PHOTO); }
            }
        }
    }
}

