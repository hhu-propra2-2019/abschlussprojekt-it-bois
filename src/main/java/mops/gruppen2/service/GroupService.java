package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.Event;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GroupService {

	/**
	 * Konstruiert eine vollständige Gruppe aus Events, welche dieselbe Gruppe betreffen.
	 *
	 * @param event Initiales CreateGroup-Event
	 * @param eventList Die restlichen Events für diese Gruppe
	 * @return Gruppe auf aktuellem Stand
	 */
	Group buildGroupFromEvents(CreateGroupEvent event, List<Event> eventList){
		Group newGroup = new Group(event);

		eventList.forEach(newGroup::applyEvent);

		return newGroup;
	}
}
