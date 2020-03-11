package mops.gruppen2.service;

import mops.gruppen2.domain.Exceptions.GroupDoesNotExistException;
import mops.gruppen2.domain.Exceptions.UserNotFoundException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteGroupEvent;
import mops.gruppen2.domain.event.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.configuration.IMockitoConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GroupServiceTest {
    GroupService groupService;

    @BeforeEach
    public void setUp() {
        groupService = new GroupService(mock(EventService.class));
    }

    @Test
    void applyEventOnGroupThatIsDeleted() throws Exception {
        List<Event> eventList = new ArrayList<>();

        eventList.add(new CreateGroupEvent(1, 10L, "prof1", "hi", "foo"));

        eventList.add(new DeleteGroupEvent(44, 10, "loescher78"));

        eventList.add(new AddUserEvent(900L, 10L, "Ulli", "Ulli", "Honnis", "FC@B.de"));


        Assertions.assertThrows(GroupDoesNotExistException.class, () -> {
            groupService.projectEventList(eventList);
        });
    }

    @Test
    void returnDeletedGroup() throws Exception {
        List<Event> eventList = new ArrayList<>();

        eventList.add(new CreateGroupEvent(1, 10L, "prof1", "hi", "foo"));

        eventList.add(new DeleteGroupEvent(44, 10, "loescher78"));

        List<Group> list = new ArrayList<>();

        assertThat(groupService.projectEventList(eventList)).isEqualTo(list);
    }

    @Test
    void rightClassForSucsessfulGroup() throws Exception {
        List<Event> eventList = new ArrayList<>();

        eventList.add(new CreateGroupEvent(1, 10L, "prof1", "hi", "foo"));

        eventList.add(new AddUserEvent(900L, 10L, "Ulli", "Ulli", "Honnis", "FC@B.de"));

        assertThat(groupService.projectEventList(eventList).get(0)).isInstanceOf(Group.class);
    }

}