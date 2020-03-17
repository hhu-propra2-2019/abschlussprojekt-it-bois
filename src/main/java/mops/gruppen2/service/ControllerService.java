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
        Long groupID = eventService.checkGroup();
        Visibility visibility1;
        if (visibility) {
            visibility1 = Visibility.PUBLIC;
        } else {
            visibility1 = Visibility.PRIVATE;
            createInviteLink(groupID);
        }
        List<Event> eventList = new ArrayList<>();
        Collections.addAll(eventList, new CreateGroupEvent(groupID, account.getName(), null, GroupType.LECTURE, visibility1),
                new AddUserEvent(groupID, account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail()),
                new UpdateRoleEvent(groupID, account.getName(), Role.ADMIN),
                new UpdateGroupTitleEvent(groupID, account.getName(), title),
                new UpdateGroupDescriptionEvent(groupID, account.getName(), description),
                new UpdateRoleEvent(groupID, account.getName(), Role.ADMIN));

        eventService.saveEventList(eventList);
    }

    private void createInviteLink(Long group_id) {
        inviteLinkRepositoryService.saveInvite(group_id, UUID.randomUUID());
    }

    public void addUser(Account account, Group group) {
        AddUserEvent addUserEvent = new AddUserEvent(eventService.checkGroup(), group.getId(), account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        eventService.saveEvent(addUserEvent);
    }
}
