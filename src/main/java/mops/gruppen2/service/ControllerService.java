package mops.gruppen2.service;

import mops.gruppen2.domain.*;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.event.*;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;

@Service
public class ControllerService {

    private final EventService eventService;
    private final UserService userService;

    public ControllerService(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     * Erzeugt eine neue Gruppe, fügt den User, der die Gruppe erstellt hat, hinzu und setzt seine Rolle als Admin fest.
     * Zudem wird der Gruppentitel und die Gruppenbeschreibung erzeugt, welche vorher der Methode übergeben wurden.
     * Aus diesen Event Objekten wird eine Liste erzeugt, welche daraufhin mithilfe des EventServices gesichert wird.
     *
     * @param account Keycloak-Account
     * @param title Gruppentitel
     * @param description Gruppenbeschreibung
     */
    public void createGroup(Account account, String title, String description, Boolean visibility) throws EventException {
        Visibility visibility1;
        Long group_id = eventService.checkGroup();

        if(visibility) {
            visibility1 = Visibility.PUBLIC;
        } else {
            visibility1 = Visibility.PRIVATE;
        }

        CreateGroupEvent createGroupEvent = new CreateGroupEvent(group_id, account.getName(), null , GroupType.LECTURE, visibility1);
        eventService.saveEvent(createGroupEvent);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());

        addUser(account, group_id);
        updateTitle(account, group_id, title);
        updateDescription(account, group_id, description);
        updateRole(user.getUser_id(), group_id);
    }

    public void addUser(Account account, Long group_id){
        AddUserEvent addUserEvent = new AddUserEvent(group_id,account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail());
        eventService.saveEvent(addUserEvent);
    }

    public void updateTitle(Account account, Long group_id, String title){
        UpdateGroupTitleEvent updateGroupTitleEvent = new UpdateGroupTitleEvent(group_id,account.getName(),title);
        eventService.saveEvent(updateGroupTitleEvent);
    }

    public void updateDescription(Account account, Long group_id, String description){
        UpdateGroupDescriptionEvent updateGroupDescriptionEvent = new UpdateGroupDescriptionEvent(group_id,account.getName(),description);
        eventService.saveEvent(updateGroupDescriptionEvent);
    }

    public void updateRole(String user_id, Long group_id) throws EventException {
        UpdateRoleEvent updateRoleEvent;
        Group group = userService.getGroupById(group_id);
        User user = null;
        for (User member : group.getMembers()) {
            if(member.getUser_id().equals(user_id)) user = member;
        }
        assert user != null;
        if(group.getRoles().get(user.getUser_id()) == Role.ADMIN) {
            updateRoleEvent = new UpdateRoleEvent(group_id, user.getUser_id(), Role.MEMBER);
        } else {
            updateRoleEvent = new UpdateRoleEvent(group_id, user.getUser_id(), Role.ADMIN);
        }
        eventService.saveEvent(updateRoleEvent);
    }

    public void deleteUser(String user_id, Long group_id) throws EventException {
        Group group = userService.getGroupById(group_id);
        User user = null;
        for (User member : group.getMembers()) {
            if(member.getUser_id().equals(user_id)) user = member;
        }
        assert user != null;
        DeleteUserEvent deleteUserEvent = new DeleteUserEvent(group_id, user.getUser_id());
        eventService.saveEvent(deleteUserEvent);
    }
    
    public void deleteGroupEvent(User user, Long group_id) {
        DeleteGroupEvent deleteGroupEvent = new DeleteGroupEvent(group_id, user.getUser_id());
        eventService.saveEvent(deleteGroupEvent);
    }
}
