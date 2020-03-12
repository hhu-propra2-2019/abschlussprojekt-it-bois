package mops.gruppen2.service;

import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.GroupDoesNotExistException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteGroupEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupService {

    private final EventService eventService;
    private final EventRepository eventRepository;

    public GroupService(EventService eventService, EventRepository eventRepository) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    public List<Event> getGroupEvents(List<Long> group_ids) {
        List<EventDTO> eventDTOS = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        for (Long group_id: group_ids) {
            eventDTOS.addAll(eventRepository.findEventDTOByGroup_id(group_id));
        }
        return events = eventService.translateEventDTOs(eventDTOS);
    }

    public List<Group> projectEventList(Map<Long, Group> groupMap, List<Event> events) throws EventException {
        for (Event event : events) {
            if (event instanceof CreateGroupEvent) {
                groupMap.put(event.getGroup_id(), new Group());
            }
            if (event instanceof DeleteGroupEvent) {
                groupMap.remove(event.getGroup_id());
            } else {
                try {
                    Group group = groupMap.get(event.getGroup_id());

                    if (group == null) {
                        throw new GroupDoesNotExistException("Gruppe " + event.getGroup_id() + " existiert nicht");
                    }

                    group.applyEvent(event);
                } catch (EventException e) {
                    if (e instanceof GroupDoesNotExistException) {
                        throw e;
                    }
                    e.printStackTrace();
                }
            }

        }

        return new ArrayList<>(groupMap.values());
    }

    public List<Group> projectEventList(List<Event> events) throws EventException {
        return projectEventList(new HashMap<>(), events);
    }
}
