package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
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
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(mock(JsonService.class), eventRepository);
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
        eventRepository.deleteAll();
        assertEquals(1L, eventService.checkGroup());
    }

    @Test
    void translateEventDTOsTest() {
        EventDTO eventDTO1 = new EventDTO(1L,1L, "killerbert", "CreateGroupEvent", "{\"type\":\"CreateGroupEvent\",\"groupId\":1,\"userId\":\"orga\",\"groupVisibility\":\"PUBLIC\",\"groupParent\":null,\"groupType\":\"SIMPLE\",\"groupUserMaximum\":2}");
        EventDTO eventDTO2 = new EventDTO(2L,2L,"jens","AddUserEvent","{\"type\":\"AddUserEvent\",\"groupId\":1,\"userId\":\"orga\",\"givenname\":\"orga\",\"familyname\":\"orga\",\"email\":\"blorga@orga.org\"}");
        List<EventDTO> eventDTOS1 = new ArrayList<>();
        eventDTOS1.add(eventDTO1);
        eventDTOS1.add(eventDTO2);
        Iterable<EventDTO> eventDTOS2 = eventDTOS1;
        List<Event> events = eventService.translateEventDTOs(eventDTOS2);
        assertTrue(events.get(0).getClass().isInstance(CreateGroupEvent.class));
    }

}
