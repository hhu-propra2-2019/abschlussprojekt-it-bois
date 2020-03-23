package mops.gruppen2.service;

import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

class GroupServiceTest {

    private GroupService groupService;

    @BeforeEach
    void setUp() {
        groupService = new GroupService(mock(EventService.class), mock(EventRepository.class));
    }


   /* @Test
    void rightClassForSuccessfulGroup() {
        List<Event> eventList = new ArrayList<>();
        eventList.add(new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE,1000L));
        eventList.add(new AddUserEvent(1L, "Ulli", "Ulli", "Honnis", "FC@B.de"));

        List<Group> groups = groupService.projectEventList(eventList);

        assertThat(groups.get(0)).isInstanceOf(Group.class);
    }*/
}
