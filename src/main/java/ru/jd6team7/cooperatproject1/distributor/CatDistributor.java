package ru.jd6team7.cooperatproject1.distributor;

import org.springframework.stereotype.Component;
import ru.jd6team7.cooperatproject1.sender.DailyReportSender;
import ru.jd6team7.cooperatproject1.sender.VolunteerSender;
import ru.jd6team7.cooperatproject1.sender.catSender.BaseCatSender;
import ru.jd6team7.cooperatproject1.sender.catSender.CatInfoPetSender;
import ru.jd6team7.cooperatproject1.sender.catSender.InfoCatShelterSender;
import ru.jd6team7.cooperatproject1.service.VisitorService;

/**
 * Распределялка по кошачим сендерам. Сообщения с базовым статусом обработаны отдельно Сообщения с
 * другими статусами обрабатываются в нужном сендере
 */
@Component
public class CatDistributor extends Distributor {

  private final BaseCatSender baseCatSender;
  private final InfoCatShelterSender infoCatShelterSender;

  private final CatInfoPetSender catInfoPetSender;

  public CatDistributor(BaseCatSender baseCatSender,
                        InfoCatShelterSender infoCatShelterSender,
                        VolunteerSender volunteerSender,
                        VisitorService visitorService,
                        DailyReportSender dailyReportSender, CatInfoPetSender catInfoPetSender) {
    super(volunteerSender, visitorService, dailyReportSender);
    this.baseCatSender = baseCatSender;
    this.infoCatShelterSender = infoCatShelterSender;
    this.catInfoPetSender = catInfoPetSender;
  }

  @Override
  protected void sendInfoIntro(long chatId) {
    infoCatShelterSender.sendIntro(chatId);
  }

  @Override
  protected void sendPetIntro(long chatId) {
    catInfoPetSender.sendIntro(chatId);
  }

  @Override
  protected void sendIntro(long chatId) {
    baseCatSender.sendIntro(chatId);
  }

  @Override
  protected void process(long chatId, String message) {
    baseCatSender.process(chatId, message);
  }

  @Override
  protected void processShelter(long chatId, String message) {
    infoCatShelterSender.process(chatId, message);
  }

  @Override
  protected void processPet(long chatId, String message) {
    catInfoPetSender.process(chatId, message);
  }

  @Override
  public String command() {
    return "/cat";
  }

}
