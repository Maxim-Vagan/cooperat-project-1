package ru.jd6team7.cooperatproject1.sender.catSender;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.Sender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/** Обработка всех поступивших сообщений от пользователя со статусом SHELTER_INFO
 */
@Component
public class InfoCatShelterSender extends Sender {
    private final Visitor.MessageStatus status = Visitor.MessageStatus.SHELTER_INFO;
    private final String INTRO_INFO = "Я с радостью поделюсь с вами информации о нас. \r\n" +
            "* Общая информация о приюте ( /infoAboutUs)\r\n" +
            "* Расписание работы, адрес, схема проезда ( /address)\r\n" +
            "* Техника безопасности ( /safety)\r\n" +
            "* Позвать на помощь ( /help)\r\n" +
            "* Для возврата назад: ( /back)";

    private final String ABOUT_US = "Мы - Кошачий рай. Наша цель - чтобы любая бездомная душа нашла своих любящих хозяев. Кошачий рай. Рай там, где мы!";
    private final String ADDRESS_AND_SCHEDULE = "Мы находимся по адресу Лебедева 35. Работаем круглочуточно, без выходных";
    private final String ABOUT_SAFETY = "Кошек не кормить! Пальцы в клетку не совать! Персонал не дразнить!";

    public InfoCatShelterSender(VisitorService visitorService, TelegramBot telegramBot) {
        super(visitorService, telegramBot);
    }
    @Override
    public void sendIntro(long chatId) {
        visitorService.updateMessageStatus(chatId, status);
        sendMessage(chatId, INTRO_INFO);
    }
    public void process(long chatId, String message) {
        switch (message) {
            case "/infoAboutUs" -> sendMessage(chatId, ABOUT_US);
            case "/address" -> sendMessage(chatId, ADDRESS_AND_SCHEDULE);
            case "/safety" -> sendMessage(chatId, ABOUT_SAFETY);
            default -> getIncorrectRequest(chatId);
        }
    }
}
