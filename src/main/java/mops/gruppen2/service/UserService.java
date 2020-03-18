package mops.gruppen2.service;

import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//Hallo
@Service
public class UserService {

    final EventRepository eventRepository;
    final GroupService groupService;

    public UserService(EventRepository eventRepository, GroupService groupService) {
        this.eventRepository = eventRepository;
        this.groupService = groupService;
    }

    //Test nötig??

    public List<Group> getUserGroups(User user) throws EventException {
        List<Long> group_ids = eventRepository.findGroup_idsWhereUser_id(user.getUser_id());
        List<Event> events = groupService.getGroupEvents(group_ids);
        List<Group> groups = groupService.projectEventList(events);
        List<Group> newGroups = new ArrayList<>();
        for (Group group : groups) {
            if (group.getMembers().contains(user)) {
                newGroups.add(group);
            }
        }
        return newGroups;
    }

    public Group getGroupById(Long group_id) throws EventException {
        List<Long> group_ids = new ArrayList<>();
        group_ids.add(group_id);
        List<Event> events = groupService.getGroupEvents(group_ids);
        return groupService.projectEventList(events).get(0);
    }
}
