package ru.jd6team7.cooperatproject1.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import ru.jd6team7.cooperatproject1.distributor.DogDistributor;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.service.VisitorService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class BotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final VisitorService visitorService;
    private final DogDistributor dogDistributor;
    private final String START_MESSAGE = "Здравствуйте. Я бот-ассистент приюта для животных. Сделан, чтобы творить добро." +
            "Выберите, какой приют вас интересует:\r\n" +
            "* Приют для собак \"Собачий рай\" (/dog)\r\n" +
            "* Приют для кошек \"Кошачий рай\" (/cat)";
    public BotUpdatesListener (TelegramBot telegramBot, VisitorService visitorService, DogDistributor dogDistributor) {
        this.telegramBot = telegramBot;
        this.visitorService = visitorService;
        this.dogDistributor = dogDistributor;
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
     * Обрабатывает базовые команды и распределяет по дистрибьютерам приютов.
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
                String message = parseNotificationTask(update.message().text());
                Visitor visitor = visitorService.findVisitor(update.message().chat().id());
            if(visitor == null) {
                visitorService.addVisitor(update.message().chat().id());
                telegramBot.execute(new SendMessage(update.message().chat().id(), START_MESSAGE));
            } else {
                if(message.equals("/start") || message.equals("/anotherShelter")) {
                    telegramBot.execute(new SendMessage(update.message().chat().id(), START_MESSAGE));
                } else if (message.equals("/dog")) {
                    visitorService.updateShelterStatus(update.message().chat().id(), Visitor.ShelterStatus.DOG);
                    dogDistributor.getDistribute(update.message().chat().id(), message);
                } else if (message.equals("/cat")) {

                } else {
                    if(visitor.getShelterStatus().equals(Visitor.ShelterStatus.DOG)) {
                        dogDistributor.getDistribute(update.message().chat().id(), message);
                    } if (visitor.getShelterStatus().equals(Visitor.ShelterStatus.CAT)) {

                    }
                }
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


}
