package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteGroupEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.event.UpdateGroupDescriptionEvent;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
import mops.gruppen2.domain.event.UpdateRoleEvent;
import mops.gruppen2.repository.EventRepository;
import mops.gruppen2.security.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mops.gruppen2.TestBuilder.idFromNumber;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
class GroupServiceTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private JsonService jsonService;
    @Autowired
    private EventService eventService;
    private GroupService groupService;

    @BeforeEach
    void setUp() {
        groupService = new GroupService(eventService, eventRepository);
        eventRepository.deleteAll();
    }

    @Test
    void rightClassForSuccessfulGroup() {
        List<Event> eventList = new ArrayList<>();
        UUID id = UUID.randomUUID();
        eventList.add(new CreateGroupEvent(id, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE, 1000L));
        eventList.add(new AddUserEvent(id, "Ulli", "Ulli", "Honnis", "FC@B.de"));
        List<Group> groups = groupService.projectEventList(eventList);
        assertThat(groups.get(0)).isInstanceOf(Group.class);
    }

    @Test
    void getGroupEventsTest() {
        CreateGroupEvent test1 = new CreateGroupEvent(idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L);
        CreateGroupEvent test2 = new CreateGroupEvent(idFromNumber(1), "test2", null, GroupType.SIMPLE, Visibility.PUBLIC, 10L);
        eventService.saveEvent(test1);
        eventService.saveEvent(test2);
        List<UUID> longs = new ArrayList<>();
        longs.add(idFromNumber(0));
        longs.add(idFromNumber(1));
        assertThat(groupService.getGroupEvents(longs).get(0).getUserId()).isEqualTo("test1");
    }

    @Test
    void getAllGroupWithVisibilityPublicTestCreateAndDeleteSameGroup() {
        CreateGroupEvent test1 = new CreateGroupEvent(idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L);
        DeleteGroupEvent test2 = new DeleteGroupEvent(idFromNumber(0), "test1");

        Group group = new Group();
        test1.apply(group);
        test2.apply(group);

        //assertThat(group.getType()).isEqualTo(null);

        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(0);
    }

    @Test
    void getAllGroupWithVisibilityPublicTestGroupPublic() {
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new DeleteGroupEvent(idFromNumber(0), "test1"));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(idFromNumber(1), "test2", Role.MEMBER));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(1);
    }

    @Test
    void getAllGroupWithVisibilityPublicTestAddSomeEvents() {
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new DeleteGroupEvent(idFromNumber(0), "test1"));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(idFromNumber(1), "test2", Role.MEMBER));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(2), "test3", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(3), "test4", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(4), "test5", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(4);
    }

    @Disabled
    @Test
    void getAllGroupWithVisibilityPublicTestIsUserInGroup() {
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new AddUserEvent(idFromNumber(0), "test1", "test", "test", "test@test"));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test2").get(0).getMembers().size()).isEqualTo(1);
    }

    @Test
    void getAllLecturesWithVisibilityPublicTest() {
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(idFromNumber(1), "test2", Role.MEMBER));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(2), "test3", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(3), "test4", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(4), "test5", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        assertThat(groupService.getAllLecturesWithVisibilityPublic().size()).isEqualTo(4);
    }

    @Disabled
    @Test
    void findGroupWithTest() {
        eventService.saveEvent(new CreateGroupEvent(idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new AddUserEvent(idFromNumber(0), "test1", "test", "test", "test@test"));
        eventService.saveEvent(new UpdateGroupTitleEvent(idFromNumber(0), "test1", "TestGroup"));
        eventService.saveEvent(new UpdateGroupDescriptionEvent(idFromNumber(0), "test1", "TestDescription"));
        eventService.saveEvent(new UpdateRoleEvent(idFromNumber(0), "test1", Role.MEMBER));
        assertThat(groupService.findGroupWith("T", new Account("jens", "a@A", "test", "peter", "mueller", null)).size()).isEqualTo(1);
    }

}
