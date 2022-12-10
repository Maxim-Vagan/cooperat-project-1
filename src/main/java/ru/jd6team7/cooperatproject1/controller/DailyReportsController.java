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
import ru.jd6team7.cooperatproject1.exceptions.GuardsListIsEmptyException;
import ru.jd6team7.cooperatproject1.model.DailyReport;
import ru.jd6team7.cooperatproject1.model.Dog;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.service.DailyReportService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/daily-reports")
public class DailyReportsController {
    private final DailyReportService dailyReportService;

    public DailyReportsController(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }

    @Operation(
            summary = "Ввод данных о Ежедневном отчёте за текущую дату данного Приюта",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DailyReport.class),
                            examples = {
                            @ExampleObject(name = "Передаваемое значение в JSON формате",
                                    value = """
                                            {
                                              "id": 0,
                                              "petID": 1,
                                              "shelterID": 1,
                                              "createDate": "2022-12-03T00:29:02",
                                              "deleteDate": null,
                                              "fileSize": 0,
                                              "mediaType": null,
                                              "photo": null,
                                              "pathToFile": null,
                                              "dayDiet": null,
                                              "mainHealth": null,
                                              "oldHebits": null,
                                              "newHebits": null
                                            }
                                            """,
                                    summary = "Пример")
                    }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DailyReport.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчётов за текущую дату не найдено"
                    )}, tags = "DailyReports"
    )
    @PostMapping("/report")
    public ResponseEntity<DailyReport> setDailyReport(@RequestBody DailyReport inpDailyReport) {
        try{
            DailyReport resultEntity = dailyReportService.addDailyReportWithEntity(inpDailyReport);
            return ResponseEntity.ok(resultEntity);
        } catch (DailyReportEmptyListException dre) {
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Вывод данных о Ежедневном отчёте по его ИД номеру",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DailyReport.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчётов за текущую дату не найдено"
                    )}, tags = "DailyReports"
    )
    @GetMapping("/report")
    public ResponseEntity<DailyReport> findDailyReport(@Parameter(description = "ИД номер Ежедневного отчёта")
                                                      @RequestParam Long inpID) {
        DailyReport resultEntity = dailyReportService.findDailyReport(inpID);
        try {
            if (resultEntity != null)
                return ResponseEntity.ok(resultEntity);
            else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Изменение данных Ежедневного отчёта",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DailyReport.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DailyReport.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчётов за текущую дату не найдено"
                    )}, tags = "DailyReports"
    )
    @PutMapping("/report")
    public ResponseEntity<DailyReport> updateDailyReport(@RequestBody DailyReport inpDailyReport){
        try {
            DailyReport resultEntity = dailyReportService.updateDailyReport(inpDailyReport);
            if (resultEntity != null)
                return ResponseEntity.ok(resultEntity);
            else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Удаление данных Eжедневного отчёта по его ИД номеру",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные удалены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DailyReport.class)
                            )
                    )}, tags = "DailyReports"
    )
    @DeleteMapping("/report")
    public ResponseEntity<Boolean> deleteDailyReport(@Parameter(description = "ИД номер Питомца") @RequestParam long ID) {
        if (dailyReportService.deleteDailyReport(ID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
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
        try {
            List<DogVisitor> resultEntity = dailyReportService.showGuardsList(inpShelterID);
            if (resultEntity != null) {
                return ResponseEntity.ok(resultEntity);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (GuardsListIsEmptyException glie) {
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
    public ResponseEntity<String> sendWarningPostToChat(@Parameter(description = "ИД чата Опекуна") @RequestParam Long inpChatID) {
        dailyReportService.makeWarningForDebtor(inpChatID);
        return ResponseEntity.ok().body("Warning Message was sent to chat " + inpChatID);
    }
}
