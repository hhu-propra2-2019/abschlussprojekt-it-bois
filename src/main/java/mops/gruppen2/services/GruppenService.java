package mops.gruppen2.services;

import mops.gruppen2.events.AddUser;
import mops.gruppen2.events.CreateGroupEvent;
import mops.gruppen2.events.DeleteUserEvent;
import mops.gruppen2.events.Event;
import mops.gruppen2.entities.Gruppe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GruppenService {

	CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L,1L,1L,"hello", "foo");
	AddUser addUser = new AddUser(1L, 1L, 1L, "jens","bendiest","jb@gmail.ru");
	DeleteUserEvent deleteUserEvent = new DeleteUserEvent(1L, 1L, 1L);

	public GruppenService(){
		List<Event> eventList = new ArrayList<>();
		eventList.add(createGroupEvent);
		eventList.add(addUser);
		eventList.add(deleteUserEvent);
		Gruppe newGroup = buildGroup(eventList);
	}

	Gruppe buildGroup(List<Event> eventList){
		Gruppe newGroup = new Gruppe();
		eventList.forEach(newGroup::applyEvent);
		return newGroup;
	}
}
