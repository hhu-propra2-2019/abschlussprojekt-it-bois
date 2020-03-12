package mops.gruppen2.service;

import lombok.EqualsAndHashCode;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.GroupDoesNotExistException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteGroupEvent;
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


    public List<Group> projectEventList(List<Event> events) throws EventException {
        Map<Long, Group> groupMap = new HashMap<>();

        for (Event event : events) {
            getOrCreateGroup(groupMap, event.getGroup_id()).applyEvent(event);
        }

        return new ArrayList<>(groupMap.values());
    }

    //
    private Group getOrCreateGroup(Map<Long, Group> groups, long group_id) {
        if (!groups.containsKey(group_id)) {
            groups.put(group_id, new Group());
        }

        return groups.get(group_id);
    }
}
