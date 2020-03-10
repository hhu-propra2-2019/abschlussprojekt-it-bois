package mops.gruppen2.service;

import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.GroupDoesNotExistException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.Event;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroupService {

    private final EventService eventService;

    public GroupService(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Konstruiert eine vollständige Gruppe aus Events, welche dieselbe Gruppe betreffen.
     *
     * @param eventList Die restlichen Events für diese Gruppe
     * @return Gruppe auf aktuellem Stand
     */
    Group buildGroupFromEvents(List<Event> eventList) throws EventException {
        Group newGroup = new Group();

        try {
            if (!(eventList.get(0) instanceof CreateGroupEvent)) {
                throw new GroupDoesNotExistException("Die Gruppe existiert nicht");
            } else {
                newGroup.applyEvent(eventList.get(0));
                eventList.remove(0);
            }
            for (Event event : eventList) {
                if (!(newGroup.getId() > 0)) {
                    throw new GroupDoesNotExistException("Die Gruppe existiert nicht");
                }
                newGroup.applyEvent(event);
            }
        } catch (EventException e) {
            if (e instanceof GroupDoesNotExistException) {
                throw e;
            }
            e.printStackTrace();
        }

        if (newGroup.getId() < 0) {
            return null;
        }
        return newGroup;
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
}
