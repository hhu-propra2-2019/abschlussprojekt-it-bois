package mops.gruppen2.service;

import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.*;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;

@Service
public class ControllerService {

    private final EventService eventService;

    public ControllerService(EventService eventService) {
        this.eventService = eventService;
    }

    public void createGroup(Account account, String title, String beschreibung) {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(eventService.checkGroup(), account.getName(), null , GroupType.LECTURE, Visibility.PUBLIC);
        AddUserEvent addUserEvent = new AddUserEvent(eventService.checkGroup(), account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail());
        UpdateGroupTitleEvent updateGroupTitleEvent = new UpdateGroupTitleEvent(eventService.checkGroup(), account.getName(), title);
        UpdateGroupDescriptionEvent updateGroupDescriptionEvent = new UpdateGroupDescriptionEvent(eventService.checkGroup(), account.getName(), beschreibung);

        eventService.saveEvent(createGroupEvent);
        eventService.saveEvent(addUserEvent);
        eventService.saveEvent(updateGroupTitleEvent);
        eventService.saveEvent(updateGroupDescriptionEvent);
    }
}
