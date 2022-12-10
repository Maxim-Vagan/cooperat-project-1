package ru.jd6team7.cooperatproject1.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.jd6team7.cooperatproject1.exceptions.DailyReportEmptyListException;
import ru.jd6team7.cooperatproject1.model.Cat;
import ru.jd6team7.cooperatproject1.model.DailyReport;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DailyReportsControllerTest {

    @LocalServerPort
    public int locPort;

    @Autowired
    private TestRestTemplate testRestTemp;

    private final DailyReport dummyDailyReport = new DailyReport() ;
    @Autowired
    private DailyReportsController dailyReports;

    @BeforeEach
    void setUp() {
        dummyDailyReport.setPetID(1L);
        dummyDailyReport.setShelterID(1);
        dummyDailyReport.setMainHealth("Хорошо");
        dummyDailyReport.setDayDiet("Сухой корм");
        dummyDailyReport.setOldHebits("Отвыкает кусать за ноги");
        dummyDailyReport.setNewHebits("Привыкает к лежанке");
    }

    @Test
    void setDailyReportTest() {
        Assertions.assertEquals(dummyDailyReport,
                testRestTemp.postForObject("http://localhost:" + locPort + "/shelter/daily-reports/report",
                        dummyDailyReport,
                        DailyReport.class)
        );
    }

    @Test
    void findDailyReportSuccessTest() {
        Assertions.assertEquals(dummyDailyReport,
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/daily-reports/report?inpID=5",
                        DailyReport.class)
        );
    }

    @Test
    void findDailyReportNotFoundTest() {
        Assertions.assertEquals(HttpStatus.NOT_FOUND,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/daily-reports/report?inpID=98",
                        DailyReport.class).getStatusCode()
        );
    }

    @Test
    void updateDailyReportSuccessTest() {
        dummyDailyReport.setId(5L);
        dummyDailyReport.setMainHealth("Активное");
        testRestTemp.put("http://localhost:" + locPort + "/shelter/daily-reports/report", dummyDailyReport);
        Assertions.assertEquals(dummyDailyReport,
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/daily-reports/report?inpID=5",
                        DailyReport.class)
        );
    }

    @Test
    void updateDailyReportNotFoundTest() {
        dummyDailyReport.setId(95L);
        dummyDailyReport.setMainHealth("Сонливое");
        Assertions.assertNull(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/daily-reports/report?inpID=95",
                        DailyReport.class)
        );
    }

    @Test
    void deleteDailyReportTest() {
        testRestTemp.delete("http://localhost:" + locPort + "/shelter/daily-reports/report?ID=5");
        LocalDateTime expectedDeletDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        Assertions.assertEquals(expectedDeletDate,
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/daily-reports/report?inpID=5",
                        DailyReport.class).getDeleteDate().truncatedTo(ChronoUnit.DAYS)
        );
    }

    @Test
    void showDailyReportsTest() {
        Assertions.assertNull(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/daily-reports/2/reports",
                        List.class)
        );
    }

    @Test
    void showGuardsListSuccessTest() {
        Assertions.assertFalse(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/daily-reports/2/guardians",
                List.class).isEmpty()
        );
    }

    @Test
    void showGuardsListNotFoundTest() {
        Assertions.assertEquals(HttpStatus.NOT_FOUND,
                testRestTemp.getForEntity("http://localhost:" + locPort + "/shelter/daily-reports/10/guardians",
                List.class).getStatusCode()
        );
    }

    @Test
    void sendWarningPostToChat() {
        Assertions.assertEquals(HttpStatus.OK,
                testRestTemp.postForEntity("http://localhost:" + locPort + "/shelter/daily-reports?inpChatID=1805591065",
                        null,
                        String.class).getStatusCode()
        );
    }
}