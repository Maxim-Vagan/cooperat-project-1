package ru.jd6team7.cooperatproject1.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.sun.xml.bind.v2.TODO;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.distributor.DogDistributor;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.DailyReportSender;
import ru.jd6team7.cooperatproject1.service.CheckAndNotify;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class BotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final VisitorService visitorService;
    private final DogDistributor dogDistributor;
    private final CheckAndNotify notifyer;
    private final DailyReportSender dailyReportSender;
    private final String START_MESSAGE = "Здравствуйте. Я бот-ассистент приюта для животных. Сделан, чтобы творить добро." +
            "Выберите, какой приют вас интересует:\r\n" +
            "* Приют для собак \"Собачий рай\" (/dog)\r\n" +
            "* Приют для кошек \"Кошачий рай\" (/cat)";

    public BotUpdatesListener(TelegramBot telegramBot, VisitorService visitorService, DogDistributor dogDistributor, CheckAndNotify notifyer, DailyReportSender dailyReportSender) {
        this.telegramBot = telegramBot;
        this.visitorService = visitorService;
        this.dogDistributor = dogDistributor;
        this.notifyer = notifyer;
        this.dailyReportSender = dailyReportSender;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Метод анализирует входящую строку сообщения
     * Паттерн на различные варианты приветствия
     */
    private String parseNotificationTask(String inpMessage) {

        if (Pattern.matches("(/[Ss]tart|[Hh]i|[Hh]ello|[Пп]ривет|[Зз]дравствуй)", inpMessage)) {
            return "/start";
        } else {
            return inpMessage;
        }
    }

    /**
     * Читает сообщения. Если пользователь новый - добавляет его в БД
     * Обрабатывает базовые команды и распределяет по дистрибьютерам приютов.
     */
    @Override
    public int process(List<Update> updates) {
//        return UpdatesListener.CONFIRMED_UPDATES_ALL;
        notifyer.startChecking();
        updates.forEach(update -> {
            Visitor visitor = visitorService.findVisitor(update.message().chat().id());
            if (visitor == null) {
                visitorService.addVisitor(update.message().chat().id());
                telegramBot.execute(new SendMessage(update.message().chat().id(), START_MESSAGE));
            } else {
                if (!visitor.getMessageStatus().equals(Visitor.MessageStatus.SEND_DAILY_REPORT)) {
                    if (update.message().text() != null) {
                        String message = parseNotificationTask(update.message().text());
                        switch (message) {
                            case "/start":
                            case "/anotherShelter":
                                telegramBot.execute(new SendMessage(update.message().chat().id(), START_MESSAGE));
                                break;
                            case "/dog":
                                visitorService.updateShelterStatus(update.message().chat().id(), Visitor.ShelterStatus.DOG);
                                dogDistributor.getDistribute(update.message().chat().id(), message);
                                break;
                            case "/cat":
                                break; //TODO: продублировать логику для котов
                            default:
                                switch (visitor.getShelterStatus()) {
                                    case DOG:
                                        dogDistributor.getDistribute(update.message().chat().id(), message);
                                    case CAT:
                                        break; //TODO: продублировать логику для котов
                                }
                                break;
                        }
                    }
                } else {
                    dailyReportSender.processUpdate(visitor.getChatId(), update);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


}
