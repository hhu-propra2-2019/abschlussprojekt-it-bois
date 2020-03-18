package mops.gruppen2.service;

import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventService = new EventService(mock(JsonService.class), eventRepository);
    }

    @Test
    void getMaxID() {
        when(eventRepository.getHighesEvent_ID()).thenReturn(42L);

        assertEquals(eventService.getMaxEvent_id(), 42L);
    }

    @Test
    void checkGroupReturnNextValue() {
        when(eventRepository.getMaxGroupID()).thenReturn(2L);

        assertEquals(eventService.checkGroup(), 3L);
    }

    @Test
    void checkGroupReturnOneIfDBIsEmpty() {
        List<EventDTO> eventDTOS = new ArrayList<>();
        when(eventRepository.findAll()).thenReturn(eventDTOS);

        assertEquals(eventService.checkGroup(), 1);
    }

    @Test
    void getDTOOffentlichTest() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(eventService.checkGroup(), "test", null, GroupType.LECTURE, Visibility.PUBLIC);
        EventDTO eventDTO = eventService.getDTO(createGroupEvent);
        assertTrue(eventDTO.isVisibility());
    }

    @Test
    void getDTOPrivatTest() {
        AddUserEvent addUserEvent = new AddUserEvent(eventService.checkGroup(), "test", "franz", "mueller", "a@a");
        EventDTO eventDTO = eventService.getDTO(addUserEvent);
        assertFalse(eventDTO.isVisibility());
    }

}
