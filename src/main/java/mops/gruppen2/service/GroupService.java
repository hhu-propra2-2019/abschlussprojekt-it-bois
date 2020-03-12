package mops.gruppen2.service;

import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Group;
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



    public List<Group> projectEventList(List<Event> events) throws EventException {
        Map<Long, Group> groupMap = new HashMap<>();

        for (Event event : events) {
            getOrCreateGroup(groupMap, event.getGroup_id()).applyEvent(event);
        }

        return new ArrayList<>(groupMap.values());
    }

    //
    private Group getOrCreateGroup(Map<Long, Group> groups, long group_id) {
        if (!groups.containsKey(group_id)) {
            groups.put(group_id, new Group());
        }

        return groups.get(group_id);
    }
}
