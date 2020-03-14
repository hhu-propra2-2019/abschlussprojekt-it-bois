package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.*;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ControllerService {

    private final EventService eventService;

    public ControllerService(EventService eventService) {
        this.eventService = eventService;
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
    public void createGroup(Account account, String title, String description) {

        List<Event> eventList = new ArrayList<>();
        Collections.addAll(eventList, new CreateGroupEvent(eventService.checkGroup(), account.getName(), null , GroupType.LECTURE, Visibility.PUBLIC),
                new AddUserEvent(eventService.checkGroup(), account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail()),
                new UpdateRoleEvent(eventService.checkGroup(), account.getName(), Role.ADMIN),
                new UpdateGroupTitleEvent(eventService.checkGroup(), account.getName(), title),
                new UpdateGroupDescriptionEvent(eventService.checkGroup(), account.getName(), description),
                new UpdateRoleEvent(eventService.checkGroup(),account.getName(), Role.ADMIN));

        eventService.saveEventList(eventList);
    }

    public void addUser(Account account, Group group){
        AddUserEvent addUserEvent = new AddUserEvent(eventService.checkGroup(),group.getId(),account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail());
        eventService.saveEvent(addUserEvent);
    }
}
