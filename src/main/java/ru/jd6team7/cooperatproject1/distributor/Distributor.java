package ru.jd6team7.cooperatproject1.distributor;

import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.sender.DailyReportSender;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/**
 * Просто родитель для распределялок. Не знаю, зачем - но красиво.
 */
public abstract class Distributor {

  private final VolunteerSender volunteerSender;
  private final VisitorService visitorService;
  private final DailyReportSender dailyReportSender;

  public Distributor(VolunteerSender volunteerSender,
      VisitorService visitorService,
      DailyReportSender dailyReportSender) {
    this.volunteerSender = volunteerSender;
    this.visitorService = visitorService;
    this.dailyReportSender = dailyReportSender;
  }

  public void getDistribute(long chatId, final String message) {
    Visitor.MessageStatus status = visitorService.findVisitor(chatId).getMessageStatus();
    switch (message) {
      case "/info" -> sendInfoIntro(chatId);
      case "/help" -> volunteerSender.sendIntro(chatId);
      case "/sendReport" -> dailyReportSender.sendIntro(chatId);
      case "/back" -> sendIntro(chatId);
      default -> {
        if (command().equals(message)) {
          sendIntro(chatId);
          break;
        }
        switch (status) {
          case BASE -> process(chatId, message);
          case SHELTER_INFO -> processShelter(chatId, message);
          case GET_CALLBACK -> volunteerSender.process(chatId, message);
        }
      }
    }
  }

  public abstract String command();

  protected abstract void sendInfoIntro(long chatId);

  protected abstract void sendIntro(long chatId);

  protected abstract void process(long chatId, String message);

  protected abstract void processShelter(long chatId, String message);

}
