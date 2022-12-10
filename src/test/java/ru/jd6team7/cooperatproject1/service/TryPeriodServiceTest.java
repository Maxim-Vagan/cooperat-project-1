package ru.jd6team7.cooperatproject1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jd6team7.cooperatproject1.exceptions.TryPeriodNotFoundException;
import ru.jd6team7.cooperatproject1.model.Cat;
import ru.jd6team7.cooperatproject1.model.Dog;
import ru.jd6team7.cooperatproject1.model.PetState;
import ru.jd6team7.cooperatproject1.model.TryPeriod;
import ru.jd6team7.cooperatproject1.repository.NotificationTaskRepository;
import ru.jd6team7.cooperatproject1.repository.TryPeriodRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TryPeriodServiceTest {

    @Mock
    private TryPeriodRepository tryPeriodRepo;
    @InjectMocks
    private TryPeriodService tryPeriod;

    private final Dog dummyDog = new Dog();
    private final Cat dummyCat = new Cat();
    private final TryPeriod dummyPeriod = new TryPeriod();

    @BeforeEach
    void setUp() {
        dummyDog.setId(1L);
        dummyDog.setPetID(1L);
        dummyDog.setShelterID(1);
        dummyDog.setPetName("Марта");
        dummyDog.setCurrentState(PetState.STAY_OUT.name());

        dummyCat.setId(1L);
        dummyCat.setPetID(2L);
        dummyCat.setShelterID(2);
        dummyCat.setPetName("Мурка");
        dummyCat.setCurrentState(PetState.STAY_OUT.name());

        dummyPeriod.setId(4L);
        dummyPeriod.setPetID(3L);
        dummyPeriod.setShelterID(2);
        dummyPeriod.setStatus(TryPeriod.TryPeriodStatus.ACTIVE);
    }

    @Test
    void addTryPeriodToVisitorDogSuccessTest() {
        when(tryPeriodRepo.getTableName(any(Integer.class), any(Long.class))).thenReturn("Dog");
//        when(petService.putDogState(any(Long.class), any(PetState.class))).thenReturn(dummyDog);
        when(tryPeriodRepo.save(any(TryPeriod.class))).thenReturn(dummyPeriod);
        assertEquals(dummyPeriod, tryPeriod.addTryPeriodToVisitor(dummyPeriod));
    }

    @Test
    void addTryPeriodToVisitorCatSuccessTest() {
        when(tryPeriodRepo.getTableName(any(Integer.class), any(Long.class))).thenReturn("Cat");
//        when(petService.putCatgState(any(Long.class), any(PetState.class))).thenReturn(dummyCat);
        when(tryPeriodRepo.save(any(TryPeriod.class))).thenReturn(dummyPeriod);
        assertEquals(dummyPeriod, tryPeriod.addTryPeriodToVisitor(dummyPeriod));
    }

    @Test
    void findTryPeriodsOfVisitorSuccessTest() {
        when(tryPeriodRepo.getTryPeriodsOfVisitor(any(Integer.class), any(Integer.class))).thenReturn(List.of(dummyPeriod));
        assertFalse(tryPeriod.findTryPeriodsOfVisitor(1, 1).isEmpty());
    }

    @Test
    void findTryPeriodsOfVisitorNotFoundTest() {
        when(tryPeriodRepo.getTryPeriodsOfVisitor(any(Integer.class), any(Integer.class))).thenReturn(List.of());
        assertThrows(TryPeriodNotFoundException.class,
                () -> tryPeriod.findTryPeriodsOfVisitor(3, 2));
    }

    @Test
    void showPivotTryPeriodsReportSuccessTest() {
        Object[] objVisitor = {1L, "Василий", "84991001112", "1@mail.ru", 1805591065};
        when(tryPeriodRepo.getInfoAboutVisitorTryPeriods(any(Integer.class))).thenReturn(new ArrayList<>(List.<Object[]>of(objVisitor)));
        assertFalse(tryPeriod.showPivotTryPeriodsReport(1).isEmpty());
    }

    @Test
    void showPivotTryPeriodsReportNotFoundTest() {
        when(tryPeriodRepo.getInfoAboutVisitorTryPeriods(any(Integer.class))).thenReturn(List.of());
        assertThrows(TryPeriodNotFoundException.class,
                () -> tryPeriod.showPivotTryPeriodsReport(3));
    }

    @Test
    void updateTryPeriodSuccessTest() {
        dummyPeriod.setStatus(TryPeriod.TryPeriodStatus.CANCELED);
        when(tryPeriodRepo.findById(any(Long.class))).thenReturn(Optional.of(dummyPeriod));
        when(tryPeriodRepo.getGuardianInfoForNotify(any(Long.class))).thenReturn("That's it");
        when(tryPeriodRepo.save(any(TryPeriod.class))).thenReturn(dummyPeriod);
        assertEquals(dummyPeriod, tryPeriod.updateTryPeriod(dummyPeriod));
    }

    @Test
    void updateTryPeriodNotFoundTest() {
        dummyPeriod.setStatus(TryPeriod.TryPeriodStatus.CANCELED);
        when(tryPeriodRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(TryPeriodNotFoundException.class,
                () -> tryPeriod.updateTryPeriod(dummyPeriod));
    }
}