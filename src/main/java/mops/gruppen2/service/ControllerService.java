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
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.UserNotFoundException;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

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
    public void createGroup(Account account, String title, String description, Boolean visibility, Long userMaximum, UUID parent) throws EventException {
        Visibility visibility1;
        UUID groupId = eventService.checkGroup();

        if (visibility) {
            visibility1 = Visibility.PUBLIC;
        } else {
            visibility1 = Visibility.PRIVATE;
        }

        CreateGroupEvent createGroupEvent = new CreateGroupEvent(groupId, account.getName(), parent, GroupType.SIMPLE, visibility1, userMaximum);
        eventService.saveEvent(createGroupEvent);

        addUser(account, groupId);
        updateTitle(account, groupId, title);
        updateDescription(account, groupId, description);
        updateRole(account.getName(), groupId);
    }

    public void createOrga(Account account, String title, String description, Boolean visibility, Boolean lecture, Long userMaximum, UUID parent, List<User> users) throws EventException {
        Visibility visibility1;
        UUID groupId = eventService.checkGroup();

        if (visibility) {
            visibility1 = Visibility.PUBLIC;
        } else {
            visibility1 = Visibility.PRIVATE;
        }

        GroupType groupType;
        if (lecture) {
            groupType = GroupType.LECTURE;
        } else {
            groupType = GroupType.SIMPLE;
        }

        CreateGroupEvent createGroupEvent = new CreateGroupEvent(groupId, account.getName(), parent, groupType, visibility1, userMaximum);
        eventService.saveEvent(createGroupEvent);

        addUser(account, groupId);
        updateTitle(account, groupId, title);
        updateDescription(account, groupId, description);
        updateRole(account.getName(), groupId);
        addUserList(users, groupId);
    }


    public void addUser(Account account, UUID groupId) {
        AddUserEvent addUserEvent = new AddUserEvent(groupId, account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        eventService.saveEvent(addUserEvent);
    }

    public void addUserList(List<User> users, UUID groupId) {
        for (User user : users) {
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

    public void deleteGroupEvent(String user_id, UUID groupId) {
        DeleteGroupEvent deleteGroupEvent = new DeleteGroupEvent(groupId, user_id);
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

    private boolean isLastAdmin(Account account, Group group){
        for (Map.Entry<String, Role> entry : group.getRoles().entrySet()){
            if (entry.getValue() == ADMIN) {
                if (!(entry.getKey().equals(account.getName()))) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getVeteranMember(Account account, Group group){
        List<User> mitglieder = group.getMembers();
        if (mitglieder.get(0).getId().equals(account.getName())){
            return mitglieder.get(1).getId();
        }
        return mitglieder.get(0).getId();
    }

}
