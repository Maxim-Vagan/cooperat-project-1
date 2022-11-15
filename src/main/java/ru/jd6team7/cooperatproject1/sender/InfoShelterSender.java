package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/** Обработка всех поступивших сообщений от пользователя со статусом SHELTER_INFO
 */
@Component
public class InfoShelterSender extends BaseSender {
    private final Visitor.MessageStatus status = Visitor.MessageStatus.SHELTER_INFO;
    private final String INTRO_INFO = "Я с радостью поделюсь с вами информации о нас. \r\n" +
            "* Общая информация о приюте (/infoAboutUs)\r\n" +
            "* Расписание работы, адрес, схема проезда (/address)\r\n" +
            "* Техника безопасности (/safety)\r\n" +
            "* Оставить контактные данные для связи (/callMe)\r\n" +
            "* Позвать волонтёра (/help)";

    public InfoShelterSender(VisitorService visitorService, TelegramBot telegramBot) {
        super(visitorService, telegramBot);
    }

    private final String ABOUT_US = "Мы - Собачий рай. Наша цель - чтобы любая бездомная душа нашла своих любящих хозяев. Собачий рай. Рай там, где мы!";
    private final String ADDRESS_ADN_SHEDULE = "Мы находимся по адресу Голованова 35. Работаем круглочуточно, без выходных";
    private final String ABOUT_SAFETY = "Собак не кормить! Пальцы в клетку не совать! Персонал не дразнить!";
    private final String INCORRECT_COMMAND = "Некорректный запрос. Попробуйте еще раз";

    @Override
    public void sendInfo(long chatId, String message) {
        switch (message) {
            case "/infoAboutUs" -> sendInfo(chatId, ABOUT_US);
            case "/address" -> sendInfo(chatId, ADDRESS_ADN_SHEDULE);
            case "/safety" -> sendInfo(chatId, ABOUT_SAFETY);
        }
    }
}
