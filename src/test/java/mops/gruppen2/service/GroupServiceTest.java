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
        Event test1 = createPublicGroupEvent(uuidFromInt(0));
        Event test2 = deleteGroupEvent(uuidFromInt(0));

        //TODO: Hier projectEventlist()?
        Group group = TestBuilder.apply(test1, test2);

        assertThat(group.getType()).isEqualTo(null);
        assertThat(groupService.getAllGroupWithVisibilityPublic("errer")).isEmpty();
    }

    @Test
    void getAllGroupWithVisibilityPublicTestGroupPublic() {
        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             deleteGroupEvent(uuidFromInt(0)),
                             createPublicGroupEvent());

        assertThat(groupService.getAllGroupWithVisibilityPublic("test1").size()).isEqualTo(1);
    }

    @Test
    void getAllGroupWithVisibilityPublicTestAddSomeEvents() {
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
        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             addUserEvent(uuidFromInt(0), "kobold"),
                             createPrivateGroupEvent(),
                             createPublicGroupEvent());

        assertThat(groupService.getAllGroupWithVisibilityPublic("kobold")).hasSize(1);
        assertThat(groupService.getAllGroupWithVisibilityPublic("peter")).hasSize(2);
    }

    @Test
    void getAllLecturesWithVisibilityPublic() {
        eventService.saveAll(createLectureEvent(),
                             createPublicGroupEvent(),
                             createLectureEvent(),
                             createLectureEvent(),
                             createLectureEvent());

        assertThat(groupService.getAllLecturesWithVisibilityPublic().size()).isEqualTo(4);
    }

    @Test
    void findGroupWith_UserMember_AllGroups() {
        eventService.saveAll(createPublicGroupEvent(uuidFromInt(0)),
                             addUserEvent(uuidFromInt(0), "jens"),
                             updateGroupTitleEvent(uuidFromInt(0)),
                             updateGroupDescriptionEvent(uuidFromInt(0)));

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
