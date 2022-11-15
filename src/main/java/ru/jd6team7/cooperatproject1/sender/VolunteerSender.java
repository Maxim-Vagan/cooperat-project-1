package ru.jd6team7.cooperatproject1.sender;

import com.pengrad.telegrambot.TelegramBot;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/** Сендер на callback. Вызывается из любого класса, дистрибьютер перехватывает сообщение /help на любом этапе
 * Ставит отметку в БД о необходимости перезвонить и собирает номер телефона
 */
@Component
public class VolunteerSender extends Sender {
    private final Visitor.MessageStatus status = Visitor.MessageStatus.GET_CALLBACK;
    private final String INTRO_WITHOUT_PHONE = "Пожалуйста, укажите номер телефона для связи. " +
            "Мы обязательно свяжемся с вами.";
    private final String DONE = "Ваш запрос получен. Мы свяжемся с вами в максимально короткие сроки";
    private final String NUMBER_NOT_FOUND = "Не удалось распознать номер телефона." +
            "Номер должен состоять из 10 или 11 цифр в любом формате. Для возврата назад: (/back)";

    public VolunteerSender(VisitorService visitorService, TelegramBot telegramBot) {
        super(visitorService, telegramBot);
    }


    /** Интро не нужно, если Юзер уже вносил ранее номер телефона.
     */

    public void process(long chatId, String message) {
        Visitor visitor = visitorService.findVisitor(chatId);
        String phoneNumber = StringUtils.getDigits(message);
        if(phoneNumber.length() != 10 && phoneNumber.length() != 11) {
            sendMessage(chatId, NUMBER_NOT_FOUND);
        } else {
            visitor.setNeedCallback(true);
            visitor.setPhoneNumber(phoneNumber);
            visitorService.updateVisitor(visitor);
            sendMessage(chatId, DONE);
            super.sendIntro(chatId);
        }

    }

    /** Обрабатывает ситуацию, когда номера телефона в БД нет.
     * Либо распознает во входящем сообении номер и занесет в БД с перезвоном
     * Либо отправит писать номер нормально без изменения статуса
     */
    @Override
    public void sendIntro(long chatId) {
        Visitor visitor = visitorService.findVisitor(chatId);
        if(visitor.getPhoneNumber() != null) {
            visitor.setNeedCallback(true);
            visitorService.updateVisitor(visitor);
            sendMessage(chatId, DONE);
            super.sendIntro(chatId);
        } else {
            visitorService.updateMessageStatus(chatId, status);
            sendMessage(chatId, INTRO_WITHOUT_PHONE);
        }
    }
}

