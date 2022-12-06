package ru.jd6team7.cooperatproject1.distributor;

import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.sender.DailyReportSender;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.sender.dogSender.BaseDogSender;
import ru.jd6team7.cooperatproject1.sender.dogSender.InfoDogShelterSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/**Распределялка по собачим сендерам. Сообщения с базовым статусом обработаны отдельно
 * Сообщения с другими статусами обрабатываются в нужном сендере
 */
@Component
public class DogDistributor extends Distributor{

  private final BaseDogSender baseDogSender;
  private final InfoDogShelterSender infoDogShelterSender;

  public DogDistributor(BaseDogSender baseDogSender,
      InfoDogShelterSender infoDogShelterSender,
      VolunteerSender volunteerSender,
      VisitorService visitorService,
      DailyReportSender dailyReportSender) {
    super(volunteerSender, visitorService, dailyReportSender);
    this.baseDogSender = baseDogSender;
    this.infoDogShelterSender = infoDogShelterSender;
  }

  @Override
  protected void sendInfoIntro(long chatId) {
    infoDogShelterSender.sendIntro(chatId);
  }

  @Override
  protected void sendIntro(long chatId) {
    baseDogSender.sendIntro(chatId);
  }

  @Override
  protected void process(long chatId, String message) {
    baseDogSender.process(chatId, message);
  }

  @Override
  protected void processShelter(long chatId, String message) {
    infoDogShelterSender.process(chatId, message);
  }

  @Override
  public String command() {
    return "/dog";
  }

}
