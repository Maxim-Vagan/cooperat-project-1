package ru.jd6team7.cooperatproject1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import ru.jd6team7.cooperatproject1.exceptions.TryPeriodNotFoundException;
import ru.jd6team7.cooperatproject1.model.TryPeriod;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TryPeriodControllerTest {

    @LocalServerPort
    public int locPort;
    @Autowired
    private TestRestTemplate testRestTemp;

    private final TryPeriod dummyTestTryPeriod = new TryPeriod();

    @BeforeEach
    void setUp() {
        dummyTestTryPeriod.setId(3L);
        dummyTestTryPeriod.setPetID(2L);
        dummyTestTryPeriod.setShelterID(2);
        dummyTestTryPeriod.setVisitorID(1);
        dummyTestTryPeriod.setStartDate(LocalDateTime.now());
        dummyTestTryPeriod.setStatus(TryPeriod.TryPeriodStatus.ACTIVE);
    }

    @Test
    void createTryPeriodTest() {
        assertEquals(dummyTestTryPeriod,
        testRestTemp.postForObject("http://localhost:" + locPort + "/shelter/try-period",
                dummyTestTryPeriod,
                TryPeriod.class)
        );
    }

    @Test
    void getTryPeriodsSuccessTest() {
        assertFalse(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/try-period/1/1",
                        List.class).isEmpty()
        );
    }

    @Test
    void getTryPeriodsNotFoundTest() {
        assertNull(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/try-period/3/10",
                List.class)
        );
    }

    @Test
    void getPivotReportSuccessTest() {
        assertFalse(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/try-period/2/pivot-report",
                        List.class).isEmpty()
                );
    }

    @Test
    void getPivotReportNotFoundTest() {
        assertNull(testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/try-period/10/pivot-report",
                List.class)
        );
    }

    @Test
    void updateTryPeriodOfVisitorSuccessTest() {
        dummyTestTryPeriod.setStatus(TryPeriod.TryPeriodStatus.PASSED);
        assertDoesNotThrow(() -> testRestTemp.put("http://localhost:" + locPort + "/shelter/try-period",
                dummyTestTryPeriod,
                TryPeriod.class)
        );
    }

    @Test
    void updateTryPeriodOfVisitorNotFoundTest() {
        dummyTestTryPeriod.setId(10L);
        dummyTestTryPeriod.setStatus(TryPeriod.TryPeriodStatus.PASSED);
        assertDoesNotThrow(() -> testRestTemp.put("http://localhost:" + locPort + "/shelter/try-period",
                dummyTestTryPeriod,
                TryPeriod.class)
        );
    }
}