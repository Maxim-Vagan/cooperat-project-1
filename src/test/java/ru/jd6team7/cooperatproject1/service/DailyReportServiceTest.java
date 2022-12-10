package ru.jd6team7.cooperatproject1.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jd6team7.cooperatproject1.exceptions.DailyReportEmptyListException;
import ru.jd6team7.cooperatproject1.model.DailyReport;
import ru.jd6team7.cooperatproject1.model.visitor.DogVisitor;
import ru.jd6team7.cooperatproject1.repository.DailyReportRepository;
import ru.jd6team7.cooperatproject1.repository.NotificationTaskRepository;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyReportServiceTest {

    @Mock
    private NotificationTaskRepository notifyRepo;
    @Mock
    private DailyReportRepository dailyRepo;
    @Mock
    private TryPeriodRepository tryPeriodRepo;
    @InjectMocks
    private DailyReportService dailyService;

    private final DailyReport dummyDailyReport = new DailyReport();

    @BeforeEach
    void setUp() {
        dummyDailyReport.setId(5L);
        dummyDailyReport.setPetID(1L);
        dummyDailyReport.setShelterID(1);
        dummyDailyReport.setMainHealth("Хорошо");
        dummyDailyReport.setDayDiet("Сухой корм");
        dummyDailyReport.setOldHebits("Отвыкает кусать за ноги");
        dummyDailyReport.setNewHebits("Привыкает к лежанке");
    }

    @Test
    void showDailyReportsSuccessTest() {
        when(dailyRepo.getCurrentDateDailyReports(any(Integer.class))).thenReturn(List.of(dummyDailyReport));
        Assertions.assertArrayEquals(List.of(dummyDailyReport).toArray(),
                dailyService.showDailyReports(1).toArray()
        );
    }

    @Test
    void showDailyReportsDailyReportEmptyListExceptionTest() {
        when(dailyRepo.getCurrentDateDailyReports(any(Integer.class))).thenReturn(List.of());
        assertThrows(DailyReportEmptyListException.class,
                () -> dailyService.showDailyReports(1)
        );
    }

    @Test
    void showGuardsListTest() {
        DogVisitor dogVisitor = new DogVisitor(1L,
                "Василий",
                "84991001112", "1@mail.ru", 1805591065L);
        Object[] objVisitor = {1L, "Василий", "84991001112", "1@mail.ru", 1805591065};
        when(tryPeriodRepo.getInfoAboutVisitorTryPeriods(any(Integer.class))).thenReturn(new ArrayList<>(List.<Object[]>of(objVisitor)));
        assertArrayEquals(List.of(dogVisitor).toArray(),
                dailyService.showGuardsList(1).toArray()
        );
    }

    @Test
    void createDailyReportTest() {
        when(dailyRepo.save(any(DailyReport.class))).thenReturn(dummyDailyReport);
        assertDoesNotThrow(() -> dailyService.createDailyReport(2L,
                2,
                LocalDateTime.now(), null, null, null, null,
                "Just Simple report Text")
        );
    }

    @Test
    void addDailyReportWithEntityTest() {
        when(dailyRepo.save(any(DailyReport.class))).thenReturn(dummyDailyReport);
        assertEquals(dummyDailyReport, dailyService.addDailyReportWithEntity(dummyDailyReport));
    }

    @Test
    void findDailyReportSuccessTest() {
        when(dailyRepo.findById(any(Long.class))).thenReturn(Optional.of(dummyDailyReport));
        assertEquals(dummyDailyReport, dailyService.findDailyReport(1L));
    }

    @Test
    void findDailyReportReturnNullTest() {
        when(dailyRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        assertNull(dailyService.findDailyReport(10L));
    }

    @Test
    void updateDailyReportSuccessTest() {
        dummyDailyReport.setMainHealth("Всё в порядке");
        when(dailyRepo.findById(any(Long.class))).thenReturn(Optional.of(dummyDailyReport));
        when(dailyRepo.save(any(DailyReport.class))).thenReturn(dummyDailyReport);
        assertEquals(dummyDailyReport, dailyService.updateDailyReport(dummyDailyReport));
    }

    @Test
    void updateDailyReportNotFoundTest() {
        when(dailyRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(DailyReportEmptyListException.class,
                () -> dailyService.updateDailyReport(dummyDailyReport));
    }

    @Test
    void deleteDailyReportSuccessTest() {
        when(dailyRepo.findById(any(Long.class))).thenReturn(Optional.of(dummyDailyReport));
        when(dailyRepo.save(any(DailyReport.class))).thenReturn(dummyDailyReport);
        assertTrue(dailyService.deleteDailyReport(dummyDailyReport.getId()));
    }

    @Test
    void deleteDailyReportNotFoundTest() {
        when(dailyRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        assertFalse(dailyService.deleteDailyReport(98L));
    }

    @Test
    void makeWarningForDebtor() {
        when(notifyRepo.addNotifOnDate(any(LocalDateTime.class),
                any(String.class), any(LocalDateTime.class), any(Long.class))).thenReturn(1);
        assertDoesNotThrow(() -> dailyService.makeWarningForDebtor(1L));
    }
}