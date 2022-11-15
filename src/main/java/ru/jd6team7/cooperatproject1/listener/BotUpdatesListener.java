package ru.jd6team7.cooperatproject1.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.distributor.Distributor;
import ru.jd6team7.cooperatproject1.model.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class BotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final VisitorService visitorService;
    private final Distributor distributor;
    private final String NEW_VISITOR = "Здравствуйте. Я бот-ассистент приюта для животных. Сделан, чтобы творить добро." +
            "* Узнать информацию о приюте (Этап 1 /info)\r\n" +
            "* Как взять питомца из приюта (Этап-2 /takePet)\r\n" +
            "* Прислать отчет о питомце (Этап-3 /sendReport)\r\n" +
            "* Позвать волонтёра (/help)";
    public BotUpdatesListener (TelegramBot telegramBot, VisitorService visitorService, Distributor distributor) {
        this.telegramBot = telegramBot;
        this.visitorService = visitorService;
        this.distributor = distributor;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /** Метод анализирует входящую строку сообщения
     * Паттерн на различные варианты приветствия
     */
    private String parseNotificationTask(String inpMessage){
        if (Pattern.matches("(/[Ss]tart|[Hh]i|[Hh]ello|[Пп]ривет|[Зз]дравствуй)", inpMessage)){
            return "/start";
        } else {
            return inpMessage;
        }
    }

    /** Читает сообщения. Если пользователь новый - добавляет его в БД
     * Делегирует обработку и распределение Дистрибьютору
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
                String message = parseNotificationTask(update.message().text());
                Visitor visitor = visitorService.findVisitor(update.message().chat().id());
            if(visitor == null) {
                visitorService.addVisitor(update.message().chat().id());
                telegramBot.execute(new SendMessage(update.message().chat().id(), NEW_VISITOR));
            } else {
                distributor.getDistribute(update.message().chat().id(), message);
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


}
