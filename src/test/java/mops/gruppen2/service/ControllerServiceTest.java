package mops.gruppen2.service;

import com.github.javafaker.Faker;
import mops.gruppen2.repository.EventRepository;
import mops.gruppen2.repository.InviteRepository;
import mops.gruppen2.security.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;

class ControllerServiceTest {

    Faker faker;
    Account account;
    ControllerService controllerService;
    EventService eventService;
    UserService userService;
    ValidationService validationService;
    EventRepository eventRepository;
    GroupService groupService;
    JsonService jsonService;
    InviteRepository inviteRepository;
    InviteService inviteService;


    @BeforeEach
    void setUp() {
        jsonService = new JsonService();
        eventRepository = mock(EventRepository.class);
        eventService = new EventService(jsonService, eventRepository);
        groupService = new GroupService(eventService, eventRepository);
        userService = new UserService(groupService, eventService);
        validationService = new ValidationService(userService, groupService);
        inviteRepository = mock(InviteRepository.class);
        inviteService = new InviteService(inviteRepository);
        controllerService = new ControllerService(eventService, userService, validationService, inviteService);
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
