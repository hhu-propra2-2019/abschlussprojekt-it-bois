package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.*;
import mops.gruppen2.repository.EventRepository;
import mops.gruppen2.security.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Rollback
@Transactional
@RunWith(MockitoJUnitRunner.class)
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
        eventList.add(new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE, 1000L));
        eventList.add(new AddUserEvent(1L, "Ulli", "Ulli", "Honnis", "FC@B.de"));
        List<Group> groups = groupService.projectEventList(eventList);
        assertThat(groups.get(0)).isInstanceOf(Group.class);
    }

    @Test
    void getGroupEventsTest() {
        CreateGroupEvent test1 = new CreateGroupEvent(eventService.checkGroup(), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L);
        CreateGroupEvent test2 = new CreateGroupEvent(eventService.checkGroup(), "test2", null, GroupType.SIMPLE, Visibility.PUBLIC, 10L);
        eventService.saveEvent(test1);
        eventService.saveEvent(test2);
        List<Long> longs = new ArrayList<>();
        longs.add(1L);
        longs.add(2L);
        assertThat(groupService.getGroupEvents(longs).get(0).getUserId()).isEqualTo("test1");
    }

    @Test
    void getAllGroupWithVisibilityPublicTestCreateAndDeleteSameGroup() {
        CreateGroupEvent test1 = new CreateGroupEvent(eventService.checkGroup(), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L);
        DeleteGroupEvent test2 = new DeleteGroupEvent(eventService.checkGroup(), "test1");
        eventService.saveEvent(test1);
        eventService.saveEvent(test2);
        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(0);
    }

    @Test
    void getAllGroupWithVisibilityPublicTestGroupPublic() {
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new DeleteGroupEvent(eventService.checkGroup(), "test1"));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(eventService.checkGroup(), "test2", Role.MEMBER));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(1);
    }

    @Test
    void getAllGroupWithVisibilityPublicTestAddSomeEvents() {
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new DeleteGroupEvent(eventService.checkGroup(), "test1"));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(eventService.checkGroup(), "test2", Role.MEMBER));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test3", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test4", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test5", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(4);
    }

    @Disabled
    @Test
    void getAllGroupWithVisibilityPublicTestIsUserInGroup() {
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new AddUserEvent(eventService.checkGroup(), "test1", "test", "test", "test@test"));
        assertThat(groupService.getAllGroupWithVisibilityPublic("test2").get(0).getMembers().size()).isEqualTo(1);
    }

    @Test
    void getAllLecturesWithVisibilityPublicTest() {
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new UpdateRoleEvent(eventService.checkGroup(), "test2", Role.MEMBER));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test3", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test4", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test5", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
        assertThat(groupService.getAllLecturesWithVisibilityPublic().size()).isEqualTo(4);
    }

    @Disabled
    @Test
    void findGroupWithTest(){
        eventService.saveEvent(new CreateGroupEvent(eventService.checkGroup(), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
        eventService.saveEvent(new AddUserEvent(eventService.checkGroup(), "test1", "test", "test", "test@test"));
        eventService.saveEvent(new UpdateGroupTitleEvent(eventService.checkGroup(), "test1", "TestGroup"));
        eventService.saveEvent(new UpdateGroupDescriptionEvent(eventService.checkGroup(), "test1", "TestDescription"));
        eventService.saveEvent(new UpdateRoleEvent( eventService.checkGroup(), "test1", Role.MEMBER));
        assertThat(groupService.findGroupWith("T", new Account("jens", "a@A", "test", "peter" , "mueller", null)).size()).isEqualTo(1);
    }

}
