package mops.gruppen2.services;

import mops.gruppen2.entities.Gruppe;
import mops.gruppen2.events.Event;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GruppenService {

	Gruppe buildGroup(List<Event> eventList){
		Gruppe newGroup = new Gruppe();
		eventList.forEach(newGroup::applyEvent);
		return newGroup;
	}
}
