package mops.gruppen2.services;

import mops.gruppen2.events.CreateGroupEvent;
import mops.gruppen2.events.Event;
import mops.gruppen2.entities.Gruppe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GruppenService {

	CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L,1L,1L,"hello", "foo");

	public GruppenService(){
		List<Event> eventList = new ArrayList<>();
		eventList.add(createGroupEvent);
		Gruppe newGroup = buildGroup(eventList);
		System.out.println(newGroup.toString());
	}

	Gruppe buildGroup(List<Event> eventList){
		Gruppe newGroup = new Gruppe();
		eventList.forEach(newGroup::applyEvent);
		return newGroup;
	}
}
