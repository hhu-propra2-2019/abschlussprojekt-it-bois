package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.TestBuilder;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Rollback
@Transactional
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
        CreateGroupEvent test1 = new CreateGroupEvent(TestBuilder.idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L);
        CreateGroupEvent test2 = new CreateGroupEvent(TestBuilder.idFromNumber(1), "test2", null, GroupType.SIMPLE, Visibility.PUBLIC, 10L);
        eventService.saveEvent(test1);
        eventService.saveEvent(test2);
        List<UUID> longs = new ArrayList<>();
        longs.add(TestBuilder.idFromNumber(0));
        longs.add(TestBuilder.idFromNumber(1));
        assertThat(groupService.getGroupEvents(longs).get(0).getUserId()).isEqualTo("test1");
    }

    @Test
    void getAllGroupWithVisibilityPublicTestCreateAndDeleteSameGroup() {
        CreateGroupEvent test1 = new CreateGroupEvent(TestBuilder.idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L);
        DeleteGroupEvent test2 = new DeleteGroupEvent(TestBuilder.idFromNumber(0), "test1");
        eventService.saveEvent(test1);
        eventService.saveEvent(test2);
        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(0);
    }

    @Test
    void getAllGroupWithVisibilityPublicTestGroupPublic() {
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new DeleteGroupEvent(TestBuilder.idFromNumber(0), "test1"));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(TestBuilder.idFromNumber(1), "test2", Role.MEMBER));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(1);
    }

    @Test
    void getAllGroupWithVisibilityPublicTestAddSomeEvents() {
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new DeleteGroupEvent(TestBuilder.idFromNumber(0), "test1"));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(TestBuilder.idFromNumber(1), "test2", Role.MEMBER));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(2), "test3", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(3), "test4", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(4), "test5", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(4);
    }

    @Disabled
    @Test
    void getAllGroupWithVisibilityPublicTestIsUserInGroup() {
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new AddUserEvent(TestBuilder.idFromNumber(0), "test1", "test", "test", "test@test"));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test2").get(0).getMembers().size()).isEqualTo(1);
    }

    @Test
    void getAllLecturesWithVisibilityPublicTest() {
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(TestBuilder.idFromNumber(1), "test2", Role.MEMBER));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(2), "test3", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(3), "test4", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(4), "test5", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        assertThat(groupService.getAllLecturesWithVisibilityPublic().size()).isEqualTo(4);
    }

    @Disabled
    @Test
    void findGroupWithTest() {
        eventService.saveEvent(new CreateGroupEvent(TestBuilder.idFromNumber(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new AddUserEvent(TestBuilder.idFromNumber(0), "test1", "test", "test", "test@test"));
        eventService.saveEvent(new UpdateGroupTitleEvent(TestBuilder.idFromNumber(0), "test1", "TestGroup"));
        eventService.saveEvent(new UpdateGroupDescriptionEvent(TestBuilder.idFromNumber(0), "test1", "TestDescription"));
        eventService.saveEvent(new UpdateRoleEvent(TestBuilder.idFromNumber(0), "test1", Role.MEMBER));
        assertThat(groupService.findGroupWith("T", new Account("jens", "a@A", "test", "peter", "mueller", null)).size()).isEqualTo(1);
    }

}
