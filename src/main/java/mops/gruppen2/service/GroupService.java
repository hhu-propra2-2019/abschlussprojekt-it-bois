package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.Event;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    /**
     * Konstruiert eine vollständige Gruppe aus Events, welche dieselbe Gruppe betreffen.
     *
     * @param eventList Die Events für diese Gruppe
     * @return Gruppe auf aktuellem Stand
     */
    public Group buildGroupFromEvents(List<Event> eventList) {
        Group newGroup = new Group();
        newGroup.apply(eventList);
        return newGroup;
    }

    public Group buildGroupFromEvent(Event event){
        Group newGroup = new Group();
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);
        newGroup.apply(eventList);
        return newGroup;
    }
}
