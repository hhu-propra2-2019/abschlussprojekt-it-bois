package mops.gruppen2.service;

import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.GroupDoesNotExistException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.Event;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupService {

    private final EventService eventService;

    public GroupService(EventService eventService) {
        this.eventService = eventService;
    }

    public List<Group> projectEventList(Map<Long, Group> groupMap, List<Event> events) {
        for (Event event : events) {
            if (event instanceof CreateGroupEvent) {
                groupMap.put(event.getGroup_id(), new Group());
            }

            try {
                Group group = groupMap.get(event.getGroup_id());

                if (group == null) {
                    throw new GroupDoesNotExistException("Gruppe " + event.getGroup_id() + " existiert nicht");
                }

                group.applyEvent(event);
            } catch (EventException e) {
                e.printStackTrace();
            }

        }

        return new ArrayList<>(groupMap.values());
    }

    public List<Group> projectEventList(List<Event> events) {
        return projectEventList(new HashMap<>(), events);
    }
}
