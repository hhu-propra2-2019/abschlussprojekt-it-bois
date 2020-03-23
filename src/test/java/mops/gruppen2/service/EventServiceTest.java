package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Rollback
@Transactional
@RunWith(MockitoJUnitRunner.class)
class EventServiceTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventService eventService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getMaxID() {
        assertEquals(5L, eventService.getMaxEvent_id()); // weil in DataSQL eine Gruppe erstellt wird
    }

    @Test
    void checkGroupReturnNextValue() {
        assertEquals(2L, eventService.checkGroup());    // weil in DataSQL eine Gruppe erstellt wird
    }

    @Test
    void checkGroupReturnOneIfDBIsEmpty() {
        //dafür muss data.sql weg
    }

    @Test
    void getDTOPublicTest() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(eventService.checkGroup(), "test", null, GroupType.LECTURE, Visibility.PUBLIC, null);
        EventDTO eventDTO = eventService.getDTO(createGroupEvent);
        assertTrue(eventDTO.isVisibility());
    }

    @Test
    void getDTOPrivateTest() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(10L, "hi",null, GroupType.SIMPLE, Visibility.PRIVATE, 20L);
        EventDTO eventDTO = eventService.getDTO(createGroupEvent);
        assertFalse(eventDTO.isVisibility());
    }

}
