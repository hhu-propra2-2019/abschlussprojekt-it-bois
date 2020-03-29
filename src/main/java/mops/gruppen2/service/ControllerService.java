package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteGroupEvent;
import mops.gruppen2.domain.event.DeleteUserEvent;
import mops.gruppen2.domain.event.UpdateGroupDescriptionEvent;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
import mops.gruppen2.domain.event.UpdateRoleEvent;
import mops.gruppen2.domain.event.UpdateUserMaxEvent;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.UserNotFoundException;
import mops.gruppen2.domain.exception.WrongFileException;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static mops.gruppen2.domain.Role.ADMIN;


@Service
public class ControllerService {

    private final EventService eventService;
    private final UserService userService;
    private final ValidationService validationService;
    private final InviteService inviteService;
    private final Logger logger;

    public ControllerService(EventService eventService, UserService userService, ValidationService validationService, InviteService inviteService) {
        this.eventService = eventService;
        this.userService = userService;
        this.validationService = validationService;
        this.inviteService = inviteService;
        this.logger = Logger.getLogger("controllerServiceLogger");
    }

    public UUID createGroup(Account account, String title, String description, Boolean isVisibilityPrivate, Boolean isLecture, Boolean isMaximumInfinite, Long userMaximum, UUID parent) {
        userMaximum = checkInfiniteUsers(isMaximumInfinite, userMaximum);

        Visibility groupVisibility = setGroupVisibility(isVisibilityPrivate);
        UUID groupId = UUID.randomUUID();

        GroupType groupType = setGroupType(isLecture);

        CreateGroupEvent createGroupEvent = new CreateGroupEvent(groupId, account.getName(), parent, groupType, groupVisibility, userMaximum);
        eventService.saveEvent(createGroupEvent);

        inviteService.createLink(groupId);

        User user = new User(account.getName(), "", "", "");

        addUser(account, groupId);
        updateRole(user, groupId);
        updateTitle(account, groupId, title);
        updateDescription(account, groupId, description);

        return groupId;
    }

    /**
     * Wie createGroup, nur das hier die Gruppe auch als Veranstaltung gesetzt werden kann und CSV Dateien mit Nutzern
     * eingelesen werden können.
     * @param account Der Nutzer der die Gruppe erstellt
     * @param title Parameter für die neue Gruppe
     * @param description Parameter für die neue Gruppe
     * @param isVisibilityPrivate Parameter für die neue Gruppe
     * @param isLecture Parameter für die neue Gruppe
     * @param isMaximumInfinite Parameter für die neue Gruppe
     * @param userMaximum Parameter für die neue Gruppe
     * @param parent Parameter für die neue Gruppe
     * @param file Parameter für die neue Gruppe
     */
    public void createGroupAsOrga(Account account, String title, String description, Boolean isVisibilityPrivate, Boolean isLecture, Boolean isMaximumInfinite, Long userMaximum, UUID parent, MultipartFile file) {
        userMaximum = checkInfiniteUsers(isMaximumInfinite, userMaximum);

        List<User> newUsers = readCsvFile(file);

        List<User> oldUsers = new ArrayList<>();
        User user = new User(account.getName(), "", "", "");
        oldUsers.add(user);

        removeOldUsersFromNewUsers(oldUsers, newUsers);

        userMaximum = adjustUserMaximum((long) newUsers.size(), 1L, userMaximum);

        UUID groupId = createGroup(account, title, description, isVisibilityPrivate, isLecture, isMaximumInfinite, userMaximum, parent);

        addUserList(newUsers, groupId);
    }

    public void addUsersFromCsv(Account account, MultipartFile file, String groupId) {
        Group group = userService.getGroupById(UUID.fromString(groupId));

        List<User> newUserList = readCsvFile(file);
        removeOldUsersFromNewUsers(group.getMembers(), newUserList);

        UUID groupUUID = getUUID(groupId);

        Long newUserMaximum = adjustUserMaximum((long) newUserList.size(), (long) group.getMembers().size(), group.getUserMaximum());
        if (newUserMaximum > group.getUserMaximum()) {
            updateMaxUser(account, groupUUID, newUserMaximum);
        }

        addUserList(newUserList, groupUUID);
    }

    public void changeMetaData(Account account, Group group, String title, String description) {
        if (!title.equals(group.getTitle())) {
            updateTitle(account, group.getId(), title);
        }

        if (!description.equals(group.getDescription())) {
            updateDescription(account, group.getId(), description);
        }
    }

    public Group getParent(UUID parentId) {
        Group parent = new Group();
        if (!idIsEmpty(parentId)) {
            parent = userService.getGroupById(parentId);
        }
        return parent;
    }

    private void removeOldUsersFromNewUsers(List<User> oldUsers, List<User> newUsers) {
        for (User oldUser : oldUsers) {
            newUsers.remove(oldUser);
        }
    }

    /**
     * Wenn die maximale Useranzahl unendlich ist, wird das Maximum auf 100000 gesetzt. Praktisch gibt es also Maximla 100000
     * Nutzer pro Gruppe.
     * @param isMaximumInfinite Gibt an ob es unendlich viele User geben soll
     * @param userMaximum Das Maximum an Usern, falls es eins gibt
     * @return Maximum an Usern
     */
    private Long checkInfiniteUsers(Boolean isMaximumInfinite, Long userMaximum) {
        isMaximumInfinite = isMaximumInfinite != null;

        if (isMaximumInfinite) {
            userMaximum = 100000L;
        }

        return userMaximum;
    }

    private Visibility setGroupVisibility(Boolean isVisibilityPrivate) {
        isVisibilityPrivate = isVisibilityPrivate != null;

        if (isVisibilityPrivate) {
            return Visibility.PRIVATE;
        } else {
            return Visibility.PUBLIC;
        }
    }

    private GroupType setGroupType(Boolean isLecture) {
        isLecture = isLecture != null;
        if (isLecture) {
            return GroupType.LECTURE;
        } else {
            return GroupType.SIMPLE;
        }
    }

    private List<User> readCsvFile(MultipartFile file) throws EventException {
        if (file == null) {
            return new ArrayList<>();
        }
        if (!file.isEmpty()) {
            try {
                List<User> userList = CsvService.read(file.getInputStream());
                return userList.stream().distinct().collect(Collectors.toList()); //filters duplicates from list
            } catch (IOException ex) {
                logger.warning("File konnte nicht gelesen werden");
                throw new WrongFileException(file.getOriginalFilename());
            }
        }
        return new ArrayList<>();
    }

    private Long adjustUserMaximum(Long newUsers, Long oldUsers, Long maxUsers) {
        if (oldUsers + newUsers > maxUsers) {
            maxUsers = oldUsers + newUsers;
        }
        return maxUsers;
    }

    public void addUser(Account account, UUID groupId) {
        AddUserEvent addUserEvent = new AddUserEvent(groupId, account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        eventService.saveEvent(addUserEvent);
    }

    public void addUserList(List<User> newUsers, UUID groupId) {
        for (User user : newUsers) {
            Group group = userService.getGroupById(groupId);
            if (group.getMembers().contains(user)) {
                logger.info("Benutzer " + user.getId() + " ist bereits in Gruppe");
            } else {
                AddUserEvent addUserEvent = new AddUserEvent(groupId, user.getId(), user.getGivenname(), user.getFamilyname(), user.getEmail());
                eventService.saveEvent(addUserEvent);
            }
        }
    }

    public void updateTitle(Account account, UUID groupId, String title) {
        UpdateGroupTitleEvent updateGroupTitleEvent = new UpdateGroupTitleEvent(groupId, account.getName(), title);
        eventService.saveEvent(updateGroupTitleEvent);
    }

    public void updateDescription(Account account, UUID groupId, String description) {
        UpdateGroupDescriptionEvent updateGroupDescriptionEvent = new UpdateGroupDescriptionEvent(groupId, account.getName(), description);
        eventService.saveEvent(updateGroupDescriptionEvent);
    }

    public void updateMaxUser(Account account, UUID groupId, Long userMaximum) {
        UpdateUserMaxEvent updateUserMaxEvent = new UpdateUserMaxEvent(groupId, account.getName(), userMaximum);
        eventService.saveEvent(updateUserMaxEvent);
    }

    public void updateRole(User user, UUID groupId) throws EventException {
        UpdateRoleEvent updateRoleEvent;
        Group group = userService.getGroupById(groupId);
        validationService.throwIfNotInGroup(group, user);

        if (group.getRoles().get(user.getId()) == ADMIN) {
            updateRoleEvent = new UpdateRoleEvent(group.getId(), user.getId(), Role.MEMBER);
        } else {
            updateRoleEvent = new UpdateRoleEvent(group.getId(), user.getId(), ADMIN);
        }
        eventService.saveEvent(updateRoleEvent);
    }

    public void deleteUser(Account account, User user, Group group) throws EventException {
        changeRoleIfLastAdmin(account, group);

        validationService.throwIfNotInGroup(group, user);

        deleteUserEvent(user, group.getId());

        if (validationService.checkIfGroupEmpty(group.getId())) {
            deleteGroupEvent(user.getId(), group.getId());
        }
    }

    private void deleteUserEvent(User user, UUID groupId) {
        DeleteUserEvent deleteUserEvent = new DeleteUserEvent(groupId, user.getId());
        eventService.saveEvent(deleteUserEvent);
    }

    public void deleteGroupEvent(String userId, UUID groupId) {
        DeleteGroupEvent deleteGroupEvent = new DeleteGroupEvent(groupId, userId);
        inviteService.destroyLink(groupId);
        eventService.saveEvent(deleteGroupEvent);
    }

    public void changeRoleIfLastAdmin(Account account, Group group) {
        if (group.getMembers().size() <= 1) {
            return;
        }
        promoteVeteranMember(account, group);
    }

    private void promoteVeteranMember(Account account, Group group) {
        if (validationService.checkIfLastAdmin(account, group)) {
            User newAdmin = getVeteranMember(account, group);
            updateRole(newAdmin, group.getId());
        }
    }

    public void changeRole(Account account, User user, Group group) {
        if (user.getId().equals(account.getName())) {
            if (group.getMembers().size() <= 1) {
                validationService.throwIfLastAdmin(account, group);
            }
            promoteVeteranMember(account, group);
        }
        updateRole(user, group.getId());
    }

    private User getVeteranMember(Account account, Group group) {
        List<User> members = group.getMembers();
        String newAdminId;
        if (members.get(0).getId().equals(account.getName())) {
            newAdminId = members.get(1).getId();
        } else {
            newAdminId = members.get(0).getId();
        }
        return new User(newAdminId, "", "", "");
    }

    public UUID getUUID(String id) {
        if (id == null) {
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        } else {
            return UUID.fromString(id);
        }
    }

    public boolean idIsEmpty(UUID id) {
        if (id == null) {
            return true;
        }

        return id.toString().equals("00000000-0000-0000-0000-000000000000");
    }

}
