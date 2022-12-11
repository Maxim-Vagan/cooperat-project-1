package ru.jd6team7.cooperatproject1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.jd6team7.cooperatproject1.model.TryPeriod;
import ru.jd6team7.cooperatproject1.model.visitor.Visitor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VisitorControllerTest {

    @LocalServerPort
    public int locPort;
    @Autowired
    private TestRestTemplate testRestTemp;

    private final Visitor dummyVisitorTestExists = new Visitor();
    private final Visitor dummyVisitorTestCreateDelete = new Visitor();

    @BeforeEach
    void setUp() {
        dummyVisitorTestExists.setId(1L);
        dummyVisitorTestExists.setChatId(1805591065L);
        dummyVisitorTestExists.setShelterStatus(Visitor.ShelterStatus.DOG);
        dummyVisitorTestExists.setMessageStatus(Visitor.MessageStatus.SEND_DAILY_REPORT);
        dummyVisitorTestExists.setEmail("email@mail.ru");
        dummyVisitorTestExists.setName("Максим");
        dummyVisitorTestExists.setNeedCallback(true);
        dummyVisitorTestExists.setPhoneNumber("84997550055");

        dummyVisitorTestCreateDelete.setChatId(321L);
        dummyVisitorTestCreateDelete.setName("Иван");
        dummyVisitorTestCreateDelete.setNeedCallback(false);
        dummyVisitorTestCreateDelete.setShelterStatus(Visitor.ShelterStatus.CAT);
        dummyVisitorTestCreateDelete.setMessageStatus(Visitor.MessageStatus.BASE);
    }

    @Test
    void addVisitor() {
        Visitor actualVisitor = testRestTemp.postForObject("http://localhost:" + locPort + "/shelter/visitor/add",
                dummyVisitorTestCreateDelete,
                Visitor.class);
        assertEquals(dummyVisitorTestCreateDelete, actualVisitor);
    }

    @Test
    void findVisitor() {
        assertEquals(dummyVisitorTestExists,
                testRestTemp.getForObject("http://localhost:" + locPort + "/shelter/visitor/" +
                                dummyVisitorTestExists.getChatId() + "/find",
                        Visitor.class)
        );
    }

    @Test
    void updateVisitor() {
        dummyVisitorTestCreateDelete.setMessageStatus(Visitor.MessageStatus.SHELTER_INFO);
        assertDoesNotThrow(() -> testRestTemp.put("http://localhost:" + locPort + "/shelter/visitor/update",
                dummyVisitorTestCreateDelete,
                Visitor.class));
    }

    @Test
    void deleteVisitor() {
        assertDoesNotThrow(() -> testRestTemp.delete("http://localhost:" + locPort + "/shelter/visitor/delete",
                dummyVisitorTestCreateDelete,
                Visitor.class));
    }
}