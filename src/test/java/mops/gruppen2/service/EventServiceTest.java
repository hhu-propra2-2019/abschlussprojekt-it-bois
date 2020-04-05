package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static mops.gruppen2.TestBuilder.addUserEvent;
import static mops.gruppen2.TestBuilder.addUserEvents;
import static mops.gruppen2.TestBuilder.createPrivateGroupEvents;
import static mops.gruppen2.TestBuilder.createPublicGroupEvent;
import static mops.gruppen2.TestBuilder.createPublicGroupEvents;
import static mops.gruppen2.TestBuilder.uuidMock;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Transactional
@Rollback
class EventServiceTest {

    @Autowired
    private EventRepository eventRepository;
    private EventService eventService;
    @Autowired
    private JdbcTemplate template;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository);
        eventRepository.deleteAll();
        //noinspection SqlResolve
        template.execute("ALTER TABLE event ALTER COLUMN event_id RESTART WITH 1");
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

        EventDTO dto = eventService.getDTOFromEvent(event);

        assertThat(dto.getGroup_id()).isEqualTo(event.getGroupId().toString());
        assertThat(dto.getUser_id()).isEqualTo(event.getUserId());
        assertThat(dto.getEvent_id()).isEqualTo(null);
        assertThat(dto.getEvent_type()).isEqualTo("CreateGroupEvent");
    }

    @Test
    void getEventsOfGroup() {
        eventService.saveAll(addUserEvents(10, uuidMock(0)),
                             addUserEvents(5, uuidMock(1)));

        assertThat(eventService.getEventsOfGroup(uuidMock(0))).hasSize(10);
        assertThat(eventService.getEventsOfGroup(uuidMock(1))).hasSize(5);
    }

    @Test
    void findGroupIdsByUser() {
        eventService.saveAll(addUserEvent(uuidMock(0), "A"),
                             addUserEvent(uuidMock(1), "A"),
                             addUserEvent(uuidMock(2), "A"),
                             addUserEvent(uuidMock(3), "A"),
                             addUserEvent(uuidMock(3), "B"));

        assertThat(eventService.findGroupIdsByUser("A")).hasSize(4);
        assertThat(eventService.findGroupIdsByUser("B")).hasSize(1);
    }
}
