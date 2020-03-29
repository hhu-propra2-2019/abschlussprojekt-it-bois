package mops.gruppen2.controller;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.repository.EventRepository;
import mops.gruppen2.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static mops.gruppen2.TestBuilder.addUserEvent;
import static mops.gruppen2.TestBuilder.createPrivateGroupEvent;
import static mops.gruppen2.TestBuilder.createPublicGroupEvent;
import static mops.gruppen2.TestBuilder.deleteGroupEvent;
import static mops.gruppen2.TestBuilder.deleteUserEvent;
import static mops.gruppen2.TestBuilder.updateGroupTitleEvent;
import static mops.gruppen2.TestBuilder.uuidMock;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Transactional
@Rollback
class APIControllerTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private APIController apiController;
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
    @WithMockUser(username = "api_user", roles = "api_user")
    void updateGroup_noGroup() {
        assertThat(apiController.updateGroups(0L).getGroupList()).hasSize(0);
        assertThat(apiController.updateGroups(4L).getGroupList()).hasSize(0);
        assertThat(apiController.updateGroups(10L).getGroupList()).hasSize(0);
        assertThat(apiController.updateGroups(0L).getStatus()).isEqualTo(0);
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void updateGroup_singleGroup() {
        eventService.saveAll(createPublicGroupEvent(uuidMock(0)),
                             addUserEvent(uuidMock(0)),
                             addUserEvent(uuidMock(0)),
                             addUserEvent(uuidMock(0)),
                             addUserEvent(uuidMock(0)));

        assertThat(apiController.updateGroups(0L).getGroupList()).hasSize(1);
        assertThat(apiController.updateGroups(4L).getGroupList()).hasSize(1);
        assertThat(apiController.updateGroups(10L).getGroupList()).hasSize(0);
        assertThat(apiController.updateGroups(0L).getStatus()).isEqualTo(5);
    }


    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void updateGroup_multipleGroups() {
        eventService.saveAll(createPublicGroupEvent(uuidMock(0)),
                             addUserEvent(uuidMock(0)),
                             addUserEvent(uuidMock(0)),
                             createPrivateGroupEvent(uuidMock(1)),
                             addUserEvent(uuidMock(1)),
                             addUserEvent(uuidMock(1)),
                             addUserEvent(uuidMock(1)));

        assertThat(apiController.updateGroups(0L).getGroupList()).hasSize(2);
        assertThat(apiController.updateGroups(4L).getGroupList()).hasSize(1);
        assertThat(apiController.updateGroups(6L).getGroupList()).hasSize(1);
        assertThat(apiController.updateGroups(7L).getGroupList()).hasSize(0);
        assertThat(apiController.updateGroups(0L).getStatus()).isEqualTo(7);
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void getGroupsOfUser_noGroup() {
        assertThat(apiController.getGroupIdsOfUser("A")).isEmpty();
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void getGroupsOfUser_singleGroup() {
        eventService.saveAll(createPrivateGroupEvent(uuidMock(0)),
                             createPrivateGroupEvent(uuidMock(1)),
                             createPrivateGroupEvent(uuidMock(2)),
                             addUserEvent(uuidMock(0), "A"));

        assertThat(apiController.getGroupIdsOfUser("A")).hasSize(1);
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void getGroupsOfUser_singleGroupDeletedUser() {
        eventService.saveAll(createPrivateGroupEvent(uuidMock(0)),
                             addUserEvent(uuidMock(0), "A"),
                             deleteUserEvent(uuidMock(0), "A"));

        assertThat(apiController.getGroupIdsOfUser("A")).isEmpty();
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void getGroupsOfUser_singleDeletedGroup() {
        eventService.saveAll(createPrivateGroupEvent(uuidMock(0)),
                             addUserEvent(uuidMock(0), "A"),
                             deleteGroupEvent(uuidMock(0)));

        assertThat(apiController.getGroupIdsOfUser("A")).isEmpty();
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void getGroupsOfUser_multipleGroups() {
        eventService.saveAll(createPrivateGroupEvent(uuidMock(0)),
                             createPrivateGroupEvent(uuidMock(1)),
                             createPrivateGroupEvent(uuidMock(2)),
                             addUserEvent(uuidMock(0), "A"),
                             addUserEvent(uuidMock(0), "B"),
                             addUserEvent(uuidMock(1), "A"),
                             addUserEvent(uuidMock(2), "A"),
                             addUserEvent(uuidMock(2), "B"));

        assertThat(apiController.getGroupIdsOfUser("A")).hasSize(3);
        assertThat(apiController.getGroupIdsOfUser("B")).hasSize(2);
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void getGroupFromId_noGroup() {
        assertThat(apiController.getGroupById(uuidMock(0).toString())).isEqualTo(null);
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void getGroupFromId_singleGroup() {
        eventService.saveAll(createPrivateGroupEvent(uuidMock(0)));

        assertThat(apiController.getGroupById(uuidMock(0).toString()).getId()).isEqualTo(uuidMock(0));
    }

    @Test
    @WithMockUser(username = "api_user", roles = "api_user")
    void getGroupFromId_deletedGroup() {
        eventService.saveAll(createPrivateGroupEvent(uuidMock(0)),
                             updateGroupTitleEvent(uuidMock(0)),
                             deleteGroupEvent(uuidMock(0)));

        assertThat(apiController.getGroupById(uuidMock(0).toString()).getTitle()).isEqualTo(null);
    }
}
