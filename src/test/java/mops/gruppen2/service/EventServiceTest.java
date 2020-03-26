package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static mops.gruppen2.TestBuilder.addUserEvent;
import static mops.gruppen2.TestBuilder.addUserEvents;
import static mops.gruppen2.TestBuilder.createPrivateGroupEvent;
import static mops.gruppen2.TestBuilder.createPrivateGroupEvents;
import static mops.gruppen2.TestBuilder.createPublicGroupEvent;
import static mops.gruppen2.TestBuilder.createPublicGroupEvents;
import static mops.gruppen2.TestBuilder.updateGroupDescriptionEvent;
import static mops.gruppen2.TestBuilder.uuidFromInt;
import static org.assertj.core.api.Assertions.assertThat;

//TODO: Der ID autocounter wird nicht resettet -> Tests schlagen fehl beim nacheinanderausführen
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Transactional
@Rollback
class EventServiceTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private JsonService jsonService;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(jsonService, eventRepository);
    }

    @Test
    void saveEvent() {
        eventService.saveEvent(createPublicGroupEvent());

        assertThat(eventRepository.findAll()).hasSize(1);
    }

    @Test
    void saveAll() {
        eventService.saveAll(createPrivateGroupEvents(10));

        assertThat(eventRepository.findAll()).hasSize(10);
    }

    @Test
    void testSaveAll() {
        eventService.saveAll(createPublicGroupEvents(5),
                             createPrivateGroupEvents(5));

        assertThat(eventRepository.findAll()).hasSize(10);
    }

    @Test
    void getDTO() {
        Event event = createPublicGroupEvent();

        EventDTO dto = eventService.getDTO(event);

        assertThat(dto.getGroup_id()).isEqualTo(event.getGroupId().toString());
        assertThat(dto.getUser_id()).isEqualTo(event.getUserId());
        assertThat(dto.getEvent_id()).isEqualTo(null);
        assertThat(dto.getEvent_type()).isEqualTo("CreateGroupEvent");
    }

    @Disabled
    @Test
    void getNewEvents_createGroupEvents() {
        eventService.saveAll(createPrivateGroupEvents(10));

        assertThat(eventService.getNewEvents(0L)).hasSize(10);
        assertThat(eventService.getNewEvents(5L)).hasSize(5);
        assertThat(eventService.getNewEvents(10L)).isEmpty();
    }

    @Disabled
    @Test
    void getNewEvents_mixedEvents() {
        eventService.saveAll(createPrivateGroupEvent(uuidFromInt(0)),
                             updateGroupDescriptionEvent(uuidFromInt(0)),
                             createPrivateGroupEvent(uuidFromInt(1)),
                             updateGroupDescriptionEvent(uuidFromInt(1)));

        assertThat(eventService.getNewEvents(0L)).hasSize(4);
        assertThat(eventService.getNewEvents(1L)).hasSize(4);
        assertThat(eventService.getNewEvents(2L)).hasSize(2);
        assertThat(eventService.getNewEvents(3L)).hasSize(2);
    }

    @Disabled
    @Test
    void getMaxEvent_id() {
        eventService.saveAll(createPrivateGroupEvents(10));

        assertThat(eventService.getMaxEvent_id()).isEqualTo(10);
    }

    @Test
    void getEventsOfGroup() {
        eventService.saveAll(addUserEvents(10, uuidFromInt(0)),
                             addUserEvents(5, uuidFromInt(1)));

        assertThat(eventService.getEventsOfGroup(uuidFromInt(0))).hasSize(10);
        assertThat(eventService.getEventsOfGroup(uuidFromInt(1))).hasSize(5);
    }

    @Test
    void findGroupIdsByUser() {
        eventService.saveAll(addUserEvent(uuidFromInt(0), "A"),
                             addUserEvent(uuidFromInt(1), "A"),
                             addUserEvent(uuidFromInt(2), "A"),
                             addUserEvent(uuidFromInt(3), "A"),
                             addUserEvent(uuidFromInt(3), "B"));

        assertThat(eventService.findGroupIdsByUser("A")).hasSize(4);
        assertThat(eventService.findGroupIdsByUser("B")).hasSize(1);
    }
}
