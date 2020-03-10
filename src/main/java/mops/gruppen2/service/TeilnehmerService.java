package mops.gruppen2.service;

import mops.gruppen2.domain.User;
import mops.gruppen2.domain.event.Event;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeilnehmerService {
    GroupService groupService;
    EventService eventService;

    public TeilnehmerService(GroupService groupService, EventService eventService){
        this.eventService = eventService;
        this.groupService = groupService;
    }

    public void assignGroups(User user){
        List<Event> events = eventService.findAllEvents();

        for (Event event: events) {
            if(user.getUser_id().equals(event.getUser_id())) user.addGroup(event.getGroup_id());
        }
    }
}
