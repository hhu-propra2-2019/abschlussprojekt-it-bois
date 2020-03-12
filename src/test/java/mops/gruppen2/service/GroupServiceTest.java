package mops.gruppen2.service;

import mops.gruppen2.domain.Exceptions.GroupDoesNotExistException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteGroupEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GroupServiceTest {
    GroupService groupService;
    EventRepository eventRepository;

    @BeforeEach
    public void setUp() {
        groupService = new GroupService(mock(EventService.class), eventRepository);
    }

    @Test
    void applyEventOnGroupThatIsDeleted() throws Exception {
        List<Event> eventList = new ArrayList<>();

        eventList.add(new CreateGroupEvent(1L,"Ulli", null, GroupType.LECTURE, Visibility.PRIVATE));

        eventList.add(new DeleteGroupEvent(44, 10, "loescher78"));

        eventList.add(new AddUserEvent(900L, 10L, "Ulli", "Ulli", "Honnis", "FC@B.de"));


        Assertions.assertThrows(GroupDoesNotExistException.class, () -> {
            groupService.projectEventList(eventList);
        });
    }

    @Test
    void returnDeletedGroup() throws Exception {
        List<Event> eventList = new ArrayList<>();

        eventList.add(new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE));

        eventList.add(new DeleteGroupEvent(44, 1L, "loescher78"));

        List<Group> list = new ArrayList<>();

        assertThat(groupService.projectEventList(eventList)).isEqualTo(list);
    }

    @Test
    void rightClassForSucsessfulGroup() throws Exception {
        List<Event> eventList = new ArrayList<>();

        eventList.add(new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE));

        eventList.add(new AddUserEvent(900L, 1L, "Ulli", "Ulli", "Honnis", "FC@B.de"));

        assertThat(groupService.projectEventList(eventList).get(0)).isInstanceOf(Group.class);
    }

}