package ru.jd6team7.cooperatproject1.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
import ru.jd6team7.cooperatproject1.exceptions.TryPeriodNotFoundException;
import ru.jd6team7.cooperatproject1.model.TryPeriod;
import ru.jd6team7.cooperatproject1.service.TryPeriodService;

import java.util.List;

@RestController
@RequestMapping(path = "/try-period")
public class TryPeriodController {

    private final TryPeriodService tryPeriodService;

    public TryPeriodController(TryPeriodService tryPeriodService) {
        this.tryPeriodService = tryPeriodService;
    }

    @Operation(
            summary = "Ввод данных об Испытательном периоде (ИС) для указанного Посетителя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TryPeriod.class),
                            examples = {
                                    @ExampleObject(name = "Передаваемое значение в JSON формате",
                                    value = "{\"id\": 0, \"petID\": 1, " +
                                            "\"visitorID\": 1, " +
                                            "\"volunteerID\": null, " +
                                            "\"status\": \"ACTIVE\", " +
                                            "\"startDate\": \"2022-11-22T10:00:00Z\", " +
                                            "\"endDate\": \"2022-12-22T10:05:00Z\", " +
                                            "\"additionalEndDate\": null, " +
                                            "\"reasonDescription\": null}",
                                    summary = "Пример")
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные записаны!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TryPeriod.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный параметр запроса",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TryPeriod.class)
                            )
                    )
            },
            tags = "TryPeriod"
    )
    @PostMapping
    public ResponseEntity<TryPeriod> createTryPeriod(@RequestBody TryPeriod inpTryP) {
        TryPeriod resultEntity = tryPeriodService.addTryPeriodToVisitor(inpTryP);
        return ResponseEntity.ok(resultEntity);
    }

    @Operation(
            summary = "Вывод данных об ИС`ах указанного Посетителя данного Приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = TryPeriod.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Список ИС у данного Посетителя пуст",
                            content = @Content(
                                    mediaType = MediaType.TEXT_HTML_VALUE
                            )
                    )}, tags = "TryPeriod"
    )
    @GetMapping("/{shelterID}/{visitorID}")
    public ResponseEntity<List<TryPeriod>> getTryPeriods(@Parameter(description = "ИД номер Приюта") @PathVariable Integer shelterID,
                                                         @Parameter(description = "ИД номер Посетителя") @PathVariable Integer visitorID) {
        try {
            List<TryPeriod> resultEntity = tryPeriodService.findTryPeriodsOfVisitor(shelterID, visitorID);
            return ResponseEntity.ok(resultEntity);
        } catch (TryPeriodNotFoundException tpError) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Вывод данных об ИС`ах указанного Посетителя данного Приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные получены!",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE//,
//                                    array = @ArraySchema(schema = @Schema(implementation = TryPeriod.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Список ИС у данного Посетителя пуст",
                            content = @Content(
                                    mediaType = MediaType.TEXT_HTML_VALUE
                            )
                    )}, tags = "TryPeriod"
    )
    @GetMapping("/{shelterID}/{visitorID}/pivot-report")
    public ResponseEntity<ObjectNode> getPivotReport1(@Parameter(description = "ИД номер Приюта") @PathVariable Integer shelterID,
                                                                @Parameter(description = "ИД номер Посетителя") @PathVariable Integer visitorID) {
        try {
            ObjectNode resultEntity = tryPeriodService.showPivotTryPeriodsReport(shelterID, visitorID);
            return ResponseEntity.ok(resultEntity);
        } catch (TryPeriodNotFoundException tpError) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
