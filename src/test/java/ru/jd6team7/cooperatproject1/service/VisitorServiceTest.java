package ru.jd6team7.cooperatproject1.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;
import ru.jd6team7.cooperatproject1.repository.VisitorRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitorServiceTest {

    @Mock
    private VisitorRepository visitorRepository;
    @InjectMocks
    private VisitorService vs;

    private final Visitor testVisitor = new Visitor();

    @BeforeEach
    void setUp() {
        testVisitor.setId(10L);
        testVisitor.setName("Бетти");
        testVisitor.setPhoneNumber("9993542569");
        testVisitor.setEmail("gehvjcdjnmk");
        testVisitor.setChatId(5641684L);
        testVisitor.setNeedCallback(false);
        testVisitor.setMessageStatus(Visitor.MessageStatus.BASE);
        testVisitor.setShelterStatus(Visitor.ShelterStatus.DOG);
    }

    @Test
    void addVisitorTest() {
        when(visitorRepository.save(any())).thenReturn(testVisitor);
        Assertions.assertEquals(testVisitor, vs.addVisitor(10L));
    }

    @Test
    void addVisitorTest2() {
        when(visitorRepository.save(any())).thenReturn(testVisitor);
        Assertions.assertEquals(testVisitor, vs.addVisitor(testVisitor));
    }

    @Test
    void findVisitorTestPositive() {
        when(visitorRepository.findByChatId(10L)).thenReturn(Optional.of(testVisitor));
        Assertions.assertEquals(testVisitor, vs.findVisitor(10L));
    }

    @Test
    void findVisitorTestNegative() {
        when(visitorRepository.findByChatId(10L)).thenReturn(null);
        Assertions.assertNull(visitorRepository.findByChatId(10L));
    }

    @Test
    void updateVisitorTest() {
        vs.deleteVisitor(testVisitor);
        verify(visitorRepository).delete(testVisitor);
    }

    @Test
    void updateMessageStatusTest() {
        vs.updateMessageStatus(10L, Visitor.MessageStatus.BASE);
        verify(visitorRepository).updateMessageStatus(10L, Visitor.MessageStatus.BASE);
    }

    @Test
    void updateShelterStatusTest() {
        vs.updateShelterStatus(10L, Visitor.ShelterStatus.DOG);
        verify(visitorRepository).updateShelterStatus(10L, Visitor.ShelterStatus.DOG);
    }

}