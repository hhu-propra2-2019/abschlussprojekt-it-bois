package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GroupServiceTest {

    private GroupService groupService;

    @BeforeEach
    void setUp() {
        groupService = new GroupService(mock(EventService.class), mock(EventRepository.class));
    }


    @Test
    void rightClassForSuccessfulGroup() {
        List<Event> eventList = new ArrayList<>();
        eventList.add(new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE,1000L));
        eventList.add(new AddUserEvent(1L, "Ulli", "Ulli", "Honnis", "FC@B.de"));

        List<Group> groups = groupService.projectEventList(eventList);

        assertThat(groups.get(0)).isInstanceOf(Group.class);
    }
}
