package mops.gruppen2.service;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
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

import java.io.CharConversionException;
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
    private final Logger logger;

    public ControllerService(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
        this.logger = Logger.getLogger("controllerServiceLogger");
    }

    /**
     * Erzeugt eine neue Gruppe, fügt den User, der die Gruppe erstellt hat, hinzu und setzt seine Rolle als Admin fest.
     * Zudem wird der Gruppentitel und die Gruppenbeschreibung erzeugt, welche vorher der Methode übergeben wurden.
     * Aus diesen Event Objekten wird eine Liste erzeugt, welche daraufhin mithilfe des EventServices gesichert wird.
     *
     * @param account     Keycloak-Account
     * @param title       Gruppentitel
     * @param description Gruppenbeschreibung
     */
    public UUID createGroup(Account account, String title, String description, Boolean isVisibilityPrivate, Boolean isLecture, Boolean isMaximumInfinite, Long userMaximum, UUID parent) throws EventException {
        userMaximum = checkInfiniteUsers(isMaximumInfinite, userMaximum);

        Visibility groupVisibility = setGroupVisibility(isVisibilityPrivate);
        UUID groupId = UUID.randomUUID();

        GroupType groupType = setGroupType(isLecture);

        CreateGroupEvent createGroupEvent = new CreateGroupEvent(groupId, account.getName(), parent, groupType, groupVisibility, userMaximum);
        eventService.saveEvent(createGroupEvent);

        addUser(account, groupId);
        updateTitle(account, groupId, title);
        updateDescription(account, groupId, description);
        updateRole(account.getName(), groupId);

        return groupId;
    }

    public void createGroupAsOrga(Account account, String title, String description, Boolean isVisibilityPrivate, Boolean isLecture, Boolean isMaximumInfinite, Long userMaximum, UUID parent, MultipartFile file) throws EventException, IOException {
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

    public void addUsersFromCsv(Account account, MultipartFile file, String groupId) throws IOException{
        Group group = userService.getGroupById(UUID.fromString(groupId));

        List<User> newUserList = readCsvFile(file);
        removeOldUsersFromNewUsers(group.getMembers(), newUserList);

        UUID groupUUID = getUUID(groupId);

        Long newUserMaximum = adjustUserMaximum((long) newUserList.size(), (long) group.getMembers().size(), group.getUserMaximum());
        if (newUserMaximum > group.getUserMaximum()){
            updateMaxUser(account, groupUUID, newUserMaximum);
        }

        addUserList(newUserList, groupUUID);
    }

    public void changeMetaData(Account account, Group group, String title, String description) {
        if (!title.equals(group.getTitle())){
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

    private List<User> readCsvFile(MultipartFile file) throws EventException, IOException {
        if (!file.isEmpty()) {
            try {
                List<User> userList = CsvService.read(file.getInputStream());
                return userList.stream().distinct().collect(Collectors.toList()); //filters duplicates from list
            } catch (UnrecognizedPropertyException | CharConversionException ex) {
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

    public void updateRole(String userId, UUID groupId) throws EventException {
        UpdateRoleEvent updateRoleEvent;
        Group group = userService.getGroupById(groupId);
        User user = null;
        for (User member : group.getMembers()) {
            if (member.getId().equals(userId)) {
                user = member;
            }
        }

        if (user == null) {
            throw new UserNotFoundException(this.getClass().toString());
        }

        if (group.getRoles().get(user.getId()) == ADMIN) {
            updateRoleEvent = new UpdateRoleEvent(groupId, user.getId(), Role.MEMBER);
        } else {
            updateRoleEvent = new UpdateRoleEvent(groupId, user.getId(), ADMIN);
        }
        eventService.saveEvent(updateRoleEvent);
    }

    public void deleteUser(String userId, UUID groupId) throws EventException {
        Group group = userService.getGroupById(groupId);
        User user = null;
        for (User member : group.getMembers()) {
            if (member.getId().equals(userId)) {
                user = member;
            }
        }

        if (user == null) {
            throw new UserNotFoundException(this.getClass().toString());
        }

        DeleteUserEvent deleteUserEvent = new DeleteUserEvent(groupId, user.getId());
        eventService.saveEvent(deleteUserEvent);
    }

    public void deleteGroupEvent(String userId, UUID groupId) {
        DeleteGroupEvent deleteGroupEvent = new DeleteGroupEvent(groupId, userId);
        eventService.saveEvent(deleteGroupEvent);
    }

    public boolean passIfLastAdmin(Account account, UUID groupId) {
        Group group = userService.getGroupById(groupId);
        if (group.getMembers().size() <= 1) {
            return true;
        }

        if (isLastAdmin(account, group)) {
            String newAdminId = getVeteranMember(account, group);
            updateRole(newAdminId, groupId);
        }
        return false;
    }

    private boolean isLastAdmin(Account account, Group group) {
        for (Map.Entry<String, Role> entry : group.getRoles().entrySet()) {
            if (entry.getValue() == ADMIN) {
                if (!(entry.getKey().equals(account.getName()))) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getVeteranMember(Account account, Group group) {
        List<User> mitglieder = group.getMembers();
        if (mitglieder.get(0).getId().equals(account.getName())) {
            return mitglieder.get(1).getId();
        }
        return mitglieder.get(0).getId();
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
