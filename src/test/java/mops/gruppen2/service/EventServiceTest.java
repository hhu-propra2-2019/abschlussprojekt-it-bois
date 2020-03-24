package mops.gruppen2.service;

import mops.gruppen2.repository.EventRepository;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;

    /*@BeforeEach
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
    }*/

    /*@Test
    void getDTOOffentlichTest() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(eventService.checkGroup(), "test", null, GroupType.LECTURE, Visibility.PUBLIC, null);
        EventDTO eventDTO = eventService.getDTO(createGroupEvent);
        assertTrue(eventDTO.isVisibility());
    }

    @Test
    void getDTOPrivatTest() {
        AddUserEvent addUserEvent = new AddUserEvent(eventService.checkGroup(), "test", "franz", "mueller", "a@a");
        EventDTO eventDTO = eventService.getDTO(addUserEvent);
        assertFalse(eventDTO.isVisibility());
    }*/

}
