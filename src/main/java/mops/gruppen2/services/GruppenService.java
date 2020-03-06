package mops.gruppen2.services;

import mops.gruppen2.events.AddUser;
import mops.gruppen2.events.CreateGroupEvent;
import mops.gruppen2.events.Event;
import mops.gruppen2.entities.Gruppe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GruppenService {

	Gruppe buildGroup(List<Event> eventList){
		Gruppe newGroup = new Gruppe();
		eventList.forEach(newGroup::applyEvent);
		return newGroup;
	}
}
