package mops.gruppen2.service;

import mops.gruppen2.Gruppen2Application;
import mops.gruppen2.domain.Account;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.exception.UserNotFoundException;
import mops.gruppen2.repository.EventRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Gruppen2Application.class)
@Transactional
@Rollback
class ControllerServiceTest {

    Account account;
    Account account2;
    Account account3;
    ControllerService controllerService;
    EventService eventService;
    UserService userService;
    ValidationService validationService;
    @Autowired
    EventRepository eventRepository;
    GroupService groupService;
    @Autowired
    InviteService inviteService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository);
        groupService = new GroupService(eventService, eventRepository);
        userService = new UserService(groupService, eventService);
        validationService = new ValidationService(userService, groupService);
        controllerService = new ControllerService(eventService, userService, validationService, inviteService);
        Set<String> roles = new HashSet<>();
        roles.add("l");
        account = new Account("ich", "ich@hhu.de", "l", "ichdude", "jap", roles);
        account2 = new Account("ich2", "ich2@hhu.de", "l", "ichdude2", "jap2", roles);
        account3 = new Account("ich3", "ich3@hhu.de", "l", "ichdude3", "jap3", roles);
        eventRepository.deleteAll();
    }

    @Test
    void createPublicGroupWithNoParentAndLimitedNumberTest() {
        controllerService.createGroup(account, "test", "hi", null, null, null, 20L, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPublicGroupWithNoParentAndUnlimitedNumberTest() {
        controllerService.createGroup(account, "test", "hi", null, null, true, null, null);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        List<Group> groups = userService.getUserGroups(user);
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPrivateGroupWithNoParentAndUnlimitedNumberTest() {
        controllerService.createGroup(account, "test", "hi", true, null, true, null, null);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        List<Group> groups = userService.getUserGroups(user);
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPrivateGroupWithNoParentAndLimitedNumberTest() {
        controllerService.createGroup(account, "test", "hi", true, null, null, 20L, null);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        List<Group> groups = userService.getUserGroups(user);
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPrivateGroupWithParentAndLimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account2, "test", "hi", null, true, true, null, null, null);
        User user = new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail());
        List<Group> groups1 = userService.getUserGroups(user);
        controllerService.createGroup(account, "test", "hi", true, null, null, 20L, groups1.get(0).getId());
        User user2 = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        List<Group> groups = userService.getUserGroups(user2);
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertEquals(groups1.get(0).getId(), groups.get(0).getParent());
    }

    @Test
    void createPublicGroupWithParentAndLimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account2, "test", "hi", null, null, true, null, null, null);
        List<Group> groups1 = userService.getUserGroups(new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail()));
        controllerService.createGroup(account, "test", "hi", null, null, null, 20L, groups1.get(0).getId());
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertEquals(groups1.get(0).getId(), groups.get(0).getParent());
    }

    @Test
    void createPublicGroupWithParentAndUnlimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account2, "test", "hi", null, null, true, null, null, null);
        List<Group> groups1 = userService.getUserGroups(new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail()));
        controllerService.createGroup(account, "test", "hi", null, true, true, null, groups1.get(0).getId());
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum());
        assertEquals(groups1.get(0).getId(), groups.get(0).getParent());
    }

    @Test
    void createPrivateGroupWithParentAndUnlimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account2, "test", "hi", null, null, true, null, null, null);
        List<Group> groups1 = userService.getUserGroups(new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail()));
        controllerService.createGroup(account, "test", "hi", true, true, true, null, groups1.get(0).getId());
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum());
        assertEquals(groups1.get(0).getId(), groups.get(0).getParent());
    }

    @Test
    void createPublicOrgaGroupWithNoParentAndLimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account, "test", "hi", null, null, null, 20L, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.SIMPLE, groups.get(0).getType());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPublicOrgaGroupWithNoParentAndUnlimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account, "test", "hi", null, null, true, null, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.SIMPLE, groups.get(0).getType());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPrivateOrgaGroupWithNoParentAndLimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account, "test", "hi", true, null, null, 20L, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.SIMPLE, groups.get(0).getType());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createPrivateOrgaGroupWithNoParentAndUnlimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account, "test", "hi", true, null, true, null, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.SIMPLE, groups.get(0).getType());
        assertEquals(Visibility.PRIVATE, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createOrgaLectureGroupAndLimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account, "test", "hi", null, true, null, 20L, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.LECTURE, groups.get(0).getType());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(20L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    void createOrgaLectureGroupAndUnlimitedNumberTest() throws IOException {
        controllerService.createGroupAsOrga(account, "test", "hi", null, true, true, null, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        testTitleAndDescription(groups.get(0).getTitle(), groups.get(0).getDescription());
        assertEquals(GroupType.LECTURE, groups.get(0).getType());
        assertEquals(Visibility.PUBLIC, groups.get(0).getVisibility());
        assertEquals(100000L, groups.get(0).getUserMaximum());
        assertNull(groups.get(0).getParent());
    }

    @Test
    public void deleteUserTest() {
        controllerService.createGroup(account, "test", "hi", true, true, true, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        controllerService.addUser(account2, groups.get(0).getId());
        User user = new User(account.getName(), "", "", "");
        controllerService.deleteUser(account, user, groups.get(0));
        assertTrue(userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail())).isEmpty());
    }

    @Test
    public void updateRoleAdminTest() {
        controllerService.createGroup(account, "test", "hi", null, null, true, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        controllerService.addUser(account2, groups.get(0).getId());
        User user = new User(account.getName(), "", "", "");
        controllerService.updateRole(user, groups.get(0).getId());
        groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        assertEquals(Role.MEMBER, groups.get(0).getRoles().get(account.getName()));
    }

    @Test
    public void updateRoleMemberTest() {
        controllerService.createGroup(account, "test", "hi", null, null, true, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        controllerService.addUser(account2, groups.get(0).getId());
        User user = new User(account2.getName(), "", "", "");
        controllerService.updateRole(user, groups.get(0).getId());
        groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        assertEquals(Role.ADMIN, groups.get(0).getRoles().get(account2.getName()));
    }

    @Test
    public void updateRoleNonUserTest() {
        controllerService.createGroup(account, "test", "hi", null, null, true, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        User user = new User(account2.getName(), "", "", "");
        Throwable exception = assertThrows(UserNotFoundException.class, () -> controllerService.updateRole(user, groups.get(0).getId()));
        assertEquals("404 NOT_FOUND \"Der User wurde nicht gefunden.    (class mops.gruppen2.service.ValidationService)\"", exception.getMessage());
    }

    @Test
    public void deleteNonUserTest() {
        controllerService.createGroup(account, "test", "hi", true, null, true, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        User user = new User(account2.getName(), "", "", "");
        Throwable exception = assertThrows(UserNotFoundException.class, () -> controllerService.deleteUser(account, user, groups.get(0)));
        assertEquals("404 NOT_FOUND \"Der User wurde nicht gefunden.    (class mops.gruppen2.service.ValidationService)\"", exception.getMessage());
    }

    void testTitleAndDescription(String title, String description) {
        assertEquals("test", title);
        assertEquals("hi", description);
    }

    @Test
    void passIfLastAdminTest() {
        controllerService.createGroup(account, "test", "hi", null, null, true, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        controllerService.addUser(account2, groups.get(0).getId());
        User user = new User(account.getName(), "", "", "");
        groups = userService.getUserGroups(new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail()));
        controllerService.deleteUser(account, user, groups.get(0));
        groups = userService.getUserGroups(new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail()));
        assertEquals(Role.ADMIN, groups.get(0).getRoles().get(account2.getName()));
    }

    @Test
    void dontPassIfNotLastAdminTest() {
        controllerService.createGroup(account, "test", "hi", null, null, true, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        controllerService.addUser(account2, groups.get(0).getId());
        User user2 = new User(account2.getName(), "", "", "");
        controllerService.updateRole(user2, groups.get(0).getId());
        controllerService.addUser(account3, groups.get(0).getId());
        controllerService.changeRoleIfLastAdmin(account, groups.get(0));
        User user = new User(account.getName(), "", "", "");
        controllerService.deleteUser(account, user, groups.get(0));
        groups = userService.getUserGroups(new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail()));
        assertEquals(Role.MEMBER, groups.get(0).getRoles().get(account3.getName()));
    }

    @Test
    void getVeteranMemberTest() {
        controllerService.createGroup(account, "test", "hi", null, null, true, null, null);
        List<Group> groups = userService.getUserGroups(new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()));
        controllerService.addUser(account2, groups.get(0).getId());
        controllerService.addUser(account3, groups.get(0).getId());
        User user = new User(account.getName(), "", "", "");
        groups = userService.getUserGroups(new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail()));
        controllerService.deleteUser(account, user, groups.get(0));
        groups = userService.getUserGroups(new User(account2.getName(), account2.getGivenname(), account2.getFamilyname(), account2.getEmail()));
        assertEquals(Role.ADMIN, groups.get(0).getRoles().get(account2.getName()));
        assertEquals(Role.MEMBER, groups.get(0).getRoles().get(account3.getName()));
    }
}
