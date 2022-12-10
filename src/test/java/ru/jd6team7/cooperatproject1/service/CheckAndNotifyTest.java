package ru.jd6team7.cooperatproject1.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.jd6team7.cooperatproject1.model.NotificationTask;
import ru.jd6team7.cooperatproject1.repository.NotificationTaskRepository;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckAndNotifyTest {

    @Mock
    private NotificationTaskRepository notifTaskRepo;
    @Mock
    private TryPeriodRepository tryPeriodRepo;
    @Mock
    private TelegramBot testBot;
    @InjectMocks
    private CheckAndNotify checkAndNotify;
    private final NotificationTask taskNotify = new NotificationTask();

    @BeforeEach
    void setUp() {
        taskNotify.setId(1L);
        taskNotify.setMessageText("For Test Notify");
        taskNotify.setTelegramChatID(2L);
    }

    @Test
    void startCheckingTest() {
        assertDoesNotThrow(() -> checkAndNotify.startChecking());
    }

    @Test
    void runScheduledNotificationProcTest() {
        when(notifTaskRepo.getNotification(any(String.class))).thenReturn(List.of(taskNotify));
        when(testBot.execute(any(SendMessage.class))).thenReturn(null);
        assertDoesNotThrow(() -> checkAndNotify.runScheduledNotificationProc());
    }

    @Test
    void runCheckForDailyRepsProcTest() {
        when(tryPeriodRepo.getListQuardiansForDailyNotification(any(String.class))).thenReturn(List.of("1234"));
        when(notifTaskRepo.addNotifOnDate(any(LocalDateTime.class),
                any(String.class), any(LocalDateTime.class), any(Long.class))).thenReturn(1);
        when(tryPeriodRepo.getListDebtorsNotification()).thenReturn(List.of("должники"));
        assertDoesNotThrow(() -> checkAndNotify.runCheckForDailyRepsProc());
    }
}