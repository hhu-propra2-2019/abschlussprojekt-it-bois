package mops.gruppen2.service;

import com.github.javafaker.Faker;
import mops.gruppen2.builder.EventBuilder;
import mops.gruppen2.domain.*;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.repository.EventRepository;
import mops.gruppen2.security.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerServiceTest {
    Faker faker;
    Account account;
    ControllerService controllerService;
    EventService eventService;
    UserService userService;
    InviteLinkRepositoryService inviteLinkRepositoryService;
    EventRepository eventRepository;
    GroupService groupService;
    JsonService jsonService;



    @BeforeEach
    void setUp() {
        jsonService = new JsonService();
        eventRepository = mock(EventRepository.class);
        eventService = new EventService(jsonService, eventRepository);
        groupService = new GroupService(eventService, eventRepository);
        userService = new UserService(eventRepository,groupService);
        controllerService = new ControllerService(eventService,userService, inviteLinkRepositoryService);
        Set<String> roles = new HashSet<>();
        roles.add("l");
        account = new Account("ich", "ich@hhu.de", "l", "ichdude", "jap", roles);
    }

    @Test
    void createGroupTest() {

    }

    @Test
    void createOrga() {
    }

    @Test
    void addUser() {
    }

    @Test
    void addUserList() {
    }

    @Test
    void updateTitle() {
    }

    @Test
    void updateDescription() {
    }

    @Test
    void updateRoleTest() {

    }

    @Test
    void deleteUser() {
    }

    @Test
    void deleteGroupEvent() {
    }

    @Test
    void passIfLastAdmin() {
    }
}