package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.*;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ControllerService {

    private final EventService eventService;
    private final InviteLinkRepositoryService inviteLinkRepositoryService;

    public ControllerService(EventService eventService, InviteLinkRepositoryService inviteLinkRepositoryService) {
        this.eventService = eventService;
        this.inviteLinkRepositoryService = inviteLinkRepositoryService;
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
    public void createGroup(Account account, String title, String description, Boolean visibility) {
        Long group_id = eventService.checkGroup();
        Visibility visibility1;
        if (visibility) {
            visibility1 = Visibility.PUBLIC;
        } else {
            visibility1 = Visibility.PRIVATE;
            createInviteLink(group_id);
        }

        CreateGroupEvent createGroupEvent = new CreateGroupEvent(group_id, account.getName(), null , GroupType.LECTURE, visibility1);
        eventService.saveEvent(createGroupEvent);

        addUser(account, group_id);
        updateTitle(account, group_id, title);
        updateDescription(account, group_id, description);
        updateRole(account, group_id);
    }

    private void createInviteLink(Long group_id) {
        inviteLinkRepositoryService.saveInvite(group_id, UUID.randomUUID());
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

    public void updateRole(Account account,Long group_id){
        UpdateRoleEvent updateRoleEvent = new UpdateRoleEvent(group_id,account.getName(),Role.ADMIN);
        eventService.saveEvent(updateRoleEvent);
    }

    public void deleteUser(Account account, Long group_id){
        DeleteUserEvent deleteUserEvent = new DeleteUserEvent(group_id,account.getName());
        eventService.saveEvent(deleteUserEvent);
    }
}
