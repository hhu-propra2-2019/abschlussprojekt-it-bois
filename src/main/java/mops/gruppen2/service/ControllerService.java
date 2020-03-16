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
    public void createGroup(Account account, String title, String description, Boolean visibility) {
        Visibility visibility1;
        if (visibility){
            visibility1 = Visibility.PUBLIC;
        }else{
            visibility1 = Visibility.PRIVATE;
        }
        List<Event> eventList = new ArrayList<>();
        Group group = new Group();
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(eventService.checkGroup(), account.getName(), null , GroupType.LECTURE, visibility1);
        createGroupEvent.apply(group);
        eventList.add(createGroupEvent);
        System.out.println(group.getId() + "" + group.getVisibility().toString());;
        eventService.saveEventList(eventList);
    }

    public void addUser(Account account, Group group){
        AddUserEvent addUserEvent = new AddUserEvent(eventService.checkGroup(),group.getId(),account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail());
        eventService.saveEvent(addUserEvent);
    }
}
