package mops.gruppen2.service;

import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventServiceTest {
    EventService eventService;
    EventRepository eventRepositoryMock = mock(EventRepository.class);

    @BeforeEach
    void setUp(){
        eventService = new EventService(mock(SerializationService.class),eventRepositoryMock);
    }

    @Test
    void checkGroupTest(){
        EventDTO eventDTO = new EventDTO();
        EventDTO eventDTO1 = new EventDTO();
        eventDTO1.setGroup_id(1L);
        eventDTO.setUser_id("realer");
        eventDTO.setUser_id("faker");
        eventDTO.setGroup_id(0L);
        List<EventDTO> eventDTOS = new ArrayList<>();
        eventDTOS.add(eventDTO);
        eventDTOS.add(eventDTO1);
        when(eventRepositoryMock.findAll()).thenReturn(eventDTOS);
        assertEquals(eventDTO1.getGroup_id()+1, eventService.checkGroup());
    }
}