package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.Event;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GroupService {

	Group buildGroupFromEvents(List<Event> eventList){
		Group newGroup = new Group();

		eventList.forEach(newGroup::applyEvent);

		return newGroup;
	}
}
