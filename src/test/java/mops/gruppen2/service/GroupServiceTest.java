package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.TestBuilder;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static mops.gruppen2.TestBuilder.account;
import static mops.gruppen2.TestBuilder.addUserEvent;
import static mops.gruppen2.TestBuilder.completePrivateGroup;
import static mops.gruppen2.TestBuilder.completePrivateGroups;
import static mops.gruppen2.TestBuilder.completePublicGroups;
import static mops.gruppen2.TestBuilder.createLectureEvent;
import static mops.gruppen2.TestBuilder.createPrivateGroupEvent;
import static mops.gruppen2.TestBuilder.createPublicGroupEvent;
import static mops.gruppen2.TestBuilder.deleteGroupEvent;
import static mops.gruppen2.TestBuilder.updateGroupDescriptionEvent;
import static mops.gruppen2.TestBuilder.updateGroupTitleEvent;
import static mops.gruppen2.TestBuilder.uuidFromInt;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Transactional
@Rollback
class GroupServiceTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventService eventService;
    private GroupService groupService;

    @BeforeEach
    void setUp() {
        groupService = new GroupService(eventService, eventRepository);
        eventRepository.deleteAll();
    }

    //TODO: Wofür ist dieser Test?
    @Test
    void rightClassForSuccessfulGroup() {
        /*List<Event> eventList = new ArrayList<>();
        UUID id = UUID.randomUUID();
        eventList.add(new CreateGroupEvent(id, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE, 1000L));
        eventList.add(new AddUserEvent(id, "Ulli", "Ulli", "Honnis", "FC@B.de"));*/
        List<Event> eventList = completePrivateGroup(1);

        List<Group> groups = groupService.projectEventList(eventList);
        assertThat(groups.get(0)).isInstanceOf(Group.class);
    }

    @Test
    void projectEventList_SingleGroup() {
        List<Event> eventList = completePrivateGroup(5);

        List<Group> groups = groupService.projectEventList(eventList);

        assertThat(groups).hasSize(1);
        assertThat(groups.get(0).getMembers()).hasSize(5);
        assertThat(groups.get(0).getVisibility()).isEqualTo(Visibility.PRIVATE);
    }

    @Test
    void projectEventList_MultipleGroups() {
        List<Event> eventList = completePrivateGroups(10, 2);
        eventList.addAll(completePublicGroups(10, 5));

        List<Group> groups = groupService.projectEventList(eventList);

        assertThat(groups).hasSize(20);
        assertThat(groups.stream().map(group -> group.getMembers().size()).reduce(Integer::sum).get()).isEqualTo(70);
    }

    @Test
    void getGroupEvents() {
//        CreateGroupEvent test1 = new CreateGroupEvent(uuidFromInt(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L);
//        CreateGroupEvent test2 = new CreateGroupEvent(uuidFromInt(1), "test2", null, GroupType.SIMPLE, Visibility.PUBLIC, 10L);

        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             createPublicGroupEvent(uuidFromInt(1)),
                             createPrivateGroupEvent(uuidFromInt(2)));

        List<UUID> groupIds = Arrays.asList(uuidFromInt(0), uuidFromInt(1));

        assertThat(groupService.getGroupEvents(groupIds)).hasSize(2);
        assertThat(groupService.getGroupEvents(groupIds).get(0).getGroupId()).isEqualTo(uuidFromInt(0));
        assertThat(groupService.getGroupEvents(groupIds).get(1).getGroupId()).isEqualTo(uuidFromInt(1));
    }

    @Test
    void getAllGroupWithVisibilityPublicTestCreateAndDeleteSameGroup() {
        //CreateGroupEvent test1 = new CreateGroupEvent(uuidFromInt(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L);
        //DeleteGroupEvent test2 = new DeleteGroupEvent(uuidFromInt(0), "test1");
        Event test1 = createPublicGroupEvent(uuidFromInt(0));
        Event test2 = deleteGroupEvent(uuidFromInt(0));

        //Group group = new Group();
        //test1.apply(group);
        //test2.apply(group);

        //TODO: Hier projectEventlist()?
        Group group = TestBuilder.apply(test1, test2);

        assertThat(group.getType()).isEqualTo(null);
        assertThat(groupService.getAllGroupWithVisibilityPublic("errer")).isEmpty();
    }

    @Test
    void getAllGroupWithVisibilityPublicTestGroupPublic() {
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
//        eventService.saveEvent(new DeleteGroupEvent(uuidFromInt(0), "test1"));
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
//        eventService.saveEvent(new UpdateRoleEvent(uuidFromInt(1), "test2", Role.MEMBER)); //Wofür ist das

        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             deleteGroupEvent(uuidFromInt(0)),
                             createPublicGroupEvent());

        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(1);
    }

    @Test
    void getAllGroupWithVisibilityPublicTestAddSomeEvents() {
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
//        eventService.saveEvent(new DeleteGroupEvent(uuidFromInt(0), "test1"));
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
//        eventService.saveEvent(new UpdateRoleEvent(uuidFromInt(1), "test2", Role.MEMBER)); // Wofür?
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(2), "test3", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(3), "test4", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(4), "test5", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));

        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             deleteGroupEvent(uuidFromInt(0)),
                             createPublicGroupEvent(),
                             createPublicGroupEvent(),
                             createPublicGroupEvent(),
                             createPrivateGroupEvent());

        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(3);
    }

    @Test
    void getAllGroupWithVisibilityPublic_UserInGroup() {
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
//        eventService.saveEvent(new AddUserEvent(uuidFromInt(0), "test1", "test", "test", "test@test"));

        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             addUserEvent(uuidFromInt(0), "kobold"),
                             createPrivateGroupEvent(),
                             createPublicGroupEvent());

        //Das kommt glaube ich eher in einen Test für die Projektion
        //assertThat(groupService.getAllGroupWithVisibilityPublic("test2").get(0).getMembers().size()).isEqualTo(1);

        assertThat(groupService.getAllGroupWithVisibilityPublic("kobold")).hasSize(1);
        assertThat(groupService.getAllGroupWithVisibilityPublic("peter")).hasSize(2);
    }

    @Test
    void getAllLecturesWithVisibilityPublic() {
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(1), "test2", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
//        eventService.saveEvent(new UpdateRoleEvent(uuidFromInt(1), "test2", Role.MEMBER)); // Hä
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(2), "test3", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(3), "test4", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(4), "test5", null, GroupType.LECTURE, Visibility.PUBLIC, 10L));

        eventService.saveAll(createLectureEvent(),
                             createPublicGroupEvent(),
                             createLectureEvent(),
                             createLectureEvent(),
                             createLectureEvent());

        assertThat(groupService.getAllLecturesWithVisibilityPublic().size()).isEqualTo(4);
    }

    @Test
    void findGroupWith_UserMember_AllGroups() {
//        eventService.saveEvent(new CreateGroupEvent(uuidFromInt(0), "test1", null, GroupType.SIMPLE, Visibility.PUBLIC, 20L));
//        eventService.saveEvent(new AddUserEvent(uuidFromInt(0), "test1", "test", "test", "test@test"));
//        eventService.saveEvent(new UpdateGroupTitleEvent(uuidFromInt(0), "test1", "TestGroup"));
//        eventService.saveEvent(new UpdateGroupDescriptionEvent(uuidFromInt(0), "test1", "TestDescription"));
//        eventService.saveEvent(new UpdateRoleEvent(uuidFromInt(0), "test1", Role.MEMBER));

        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             addUserEvent(uuidFromInt(0), "jens"),
                             updateGroupTitleEvent(uuidFromInt(0)),
                             updateGroupDescriptionEvent(uuidFromInt(0)));

        //assertThat(groupService.findGroupWith("T", new Account("jens", "a@A", "test", "peter", "mueller", null)).size()).isEqualTo(1);
        assertThat(groupService.findGroupWith("", account("jens"))).isEmpty();
    }

    @Test
    void findGroupWith_UserNoMember_AllGroups() {
        eventService.saveAll(completePublicGroups(10, 0),
                             completePrivateGroups(10, 0));

        assertThat(groupService.findGroupWith("", account("jens"))).hasSize(10);
    }

    @Test
    void findGroupWith_FilterGroups() {
        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             updateGroupTitleEvent(uuidFromInt(0), "KK"),
                             updateGroupDescriptionEvent(uuidFromInt(0), "ABCDE"),
                             createPublicGroupEvent(uuidFromInt(1)),
                             updateGroupTitleEvent(uuidFromInt(1), "ABCDEFG"),
                             updateGroupDescriptionEvent(uuidFromInt(1), "KK"),
                             createPrivateGroupEvent());

        assertThat(groupService.findGroupWith("A", account("jesus"))).hasSize(2);
        assertThat(groupService.findGroupWith("F", account("jesus"))).hasSize(1);
        assertThat(groupService.findGroupWith("Z", account("jesus"))).hasSize(0);
    }

}
