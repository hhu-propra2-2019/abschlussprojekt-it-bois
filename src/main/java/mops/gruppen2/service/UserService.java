package mops.gruppen2.service;

import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    final EventRepository eventRepository;
    final GroupService groupService;

    public UserService(EventRepository eventRepository, GroupService groupService) {
        this.eventRepository = eventRepository;
        this.groupService = groupService;
    }

    //Test nötig??

    public List<Group> getUserGroups(String user_id) throws EventException {
        List<Long> group_ids = eventRepository.findGroup_idsWhereUser_id(user_id);
        List<Event> events =  groupService.getGroupEvents(group_ids);
        return groupService.projectEventList(events);
    }

    public Group getGroupById(Long group_id) throws EventException {
        List<Long> group_ids = new ArrayList<>();
        group_ids.add(group_id);
        List<Event> events = groupService.getGroupEvents(group_ids);
        return groupService.projectEventList(events).get(0);
    }
}
