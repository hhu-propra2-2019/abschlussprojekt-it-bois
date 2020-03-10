package mops.gruppen2.service;

import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.Event;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    /**
     * Konstruiert eine vollständige Gruppe aus Events, welche dieselbe Gruppe betreffen.
     *
     * @param eventList Die restlichen Events für diese Gruppe
     * @return Gruppe auf aktuellem Stand
     */
    Group buildGroupFromEvents(List<Event> eventList) {
        Group newGroup = new Group();

        try {
            for (Event event : eventList) {
                newGroup.applyEvent(event);
            }
        }catch (EventException e){
            e.printStackTrace();
        }

        return newGroup;
    }

    public Group buildGroupFromEvent(CreateGroupEvent createGroupEvent, AddUserEvent addUserEvent) {
        Group newGroup = new Group();

        try {
            newGroup.applyEvent(createGroupEvent);
            newGroup.applyEvent(addUserEvent);
        } catch (EventException e) {
            e.printStackTrace();
        }

        return newGroup;
    }
}
