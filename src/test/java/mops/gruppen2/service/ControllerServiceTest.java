package mops.gruppen2.service;

import com.github.javafaker.Faker;
import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.repository.EventRepository;
import mops.gruppen2.security.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Transactional
@Rollback
class ControllerServiceTest {
    Faker faker;
    Account account;
    ControllerService controllerService;
    EventService eventService;
    UserService userService;
    @Autowired
    EventRepository eventRepository;
    GroupService groupService;
    @Autowired
    JsonService jsonService;



    @BeforeEach
    void setUp() {
        eventService = new EventService(jsonService, eventRepository);
        groupService = new GroupService(eventService, eventRepository);
        userService = new UserService(groupService, eventService);
        controllerService = new ControllerService(eventService, userService);
        Set<String> roles = new HashSet<>();
        roles.add("l");
        account = new Account("ich", "ich@hhu.de", "l", "ichdude", "jap", roles);
    }

    @Test
    void createPublicGroupWithNoParentAndLimitedNumberTest() {
        eventRepository.deleteAll();
        controllerService.createGroup(account,"test", "hi", null, null, 20L, null);
        List<Group> groups= userService.getUserGroups(new User(account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPublicGroupWithNoParentAndUnlimitedNumberTest() {
        eventRepository.deleteAll();
        controllerService.createGroup(account,"test", "hi", null, true, null, null);
        List<Group> groups= userService.getUserGroups(new User(account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum()); //100k ist "maximum"
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPrivateGroupWithNoParentAndUnlimitedNumberTest() {
        eventRepository.deleteAll();
        controllerService.createGroup(account,"test", "hi", true, true, null, null);
        List<Group> groups= userService.getUserGroups(new User(account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum()); //100k ist "maximum"
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPrivateGroupWithNoParentAndLimitedNumberTest() {
        eventRepository.deleteAll();
        controllerService.createGroup(account,"test", "hi", true, null, 20L, null);
        List<Group> groups= userService.getUserGroups(new User(account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum()); //100k ist "maximum"
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPublicOrgaGroupWithNoParentAndLimitedNumberTest() throws IOException {
        eventRepository.deleteAll();
        controllerService.createOrga(account, "test", "hi", null, null, null, 20L, null);
        List<Group> groups= userService.getUserGroups(new User(account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.SIMPLE, groups.get(0).getType());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPublicOrgaGroupWithNoParentAndUnlimitedNumberTest() throws IOException {
        eventRepository.deleteAll();
        controllerService.createOrga(account, "test", "hi", null, null, true, null, null);
        List<Group> groups= userService.getUserGroups(new User(account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.SIMPLE, groups.get(0).getType());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPrivateOrgaGroupWithNoParentAndLimitedNumberTest() throws IOException {
        eventRepository.deleteAll();
        controllerService.createOrga(account, "test", "hi", true, null, null, 20L, null);
        List<Group> groups= userService.getUserGroups(new User(account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.SIMPLE, groups.get(0).getType());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    void testTitleAndDescription(String title, String description) {
        assertEquals("test", title);
        assertEquals("hi", description);
    }

    @Test
    void passIfLastAdmin() {
    }
}
