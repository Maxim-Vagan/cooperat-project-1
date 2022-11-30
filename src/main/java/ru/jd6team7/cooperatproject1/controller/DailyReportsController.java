package ru.jd6team7.cooperatproject1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jd6team7.cooperatproject1.exceptions.DailyReportEmptyListException;
import ru.jd6team7.cooperatproject1.model.DailyReport;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.service.DailyReportService;

import java.util.List;

@RestController
@RequestMapping(path = "/daily-reports")
public class DailyReportsController {
    private final DailyReportService dailyReportService;

    public DailyReportsController(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }

    @Operation(
            summary = "Вывод данных о Ежедневных отчётах за текущую дату данного Приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчётов за текущую дату не найдено"
                    )}, tags = "DailyReports"
    )
    @GetMapping("/{inpShelterID}/reports")
    public ResponseEntity<List<DailyReport>> showDailyReports(@Parameter(description = "ИД номер Приюта") @PathVariable Integer inpShelterID) {
        try{
            List<DailyReport> resultEntity = dailyReportService.showDailyReports(inpShelterID);
            return ResponseEntity.ok(resultEntity);
        } catch (DailyReportEmptyListException dre) {
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Вывод списка Опекунов питомцев данного Приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DogVisitor.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = " не найдено"
                    )}
            , tags = "DailyReports"
    )
    @GetMapping("/{inpShelterID}/guardians")
    public ResponseEntity<List<DogVisitor>> showGuardsList(@Parameter(description = "ИД номер Приюта") @PathVariable Integer inpShelterID) {
        List<DogVisitor> resultEntity = dailyReportService.showGuardsList(inpShelterID);
        if (resultEntity != null) {
            return ResponseEntity.ok(resultEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Отправка указанному Опекуну в чат уведомления с Предупреждением (опция Волонтёров)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.TEXT_HTML_VALUE
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.TEXT_HTML_VALUE
                            )
                    )},
            tags = "DailyReports"
    )
    @PostMapping
    public ResponseEntity<?> sendWarningPostToChat(@Parameter(description = "ИД чата Опекуна") @RequestParam Long inpChatID) {
        dailyReportService.makeWarningForDebtor(inpChatID);
        return ResponseEntity.ok().body("Warning Message was sent to chat " + inpChatID);
    }
}
