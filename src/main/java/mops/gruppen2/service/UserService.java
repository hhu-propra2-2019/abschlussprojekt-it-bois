package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//Hallo
@Service
public class UserService {

    private final EventRepository eventRepository;
    private final GroupService groupService;

    public UserService(EventRepository eventRepository, GroupService groupService) {
        this.eventRepository = eventRepository;
        this.groupService = groupService;
    }

    //Test nötig??

    public List<Group> getUserGroups(User user) throws EventException {
        List<Long> groupIds = eventRepository.findGroup_idsWhereUser_id(user.getId());
        List<Event> events = groupService.getGroupEvents(groupIds);
        List<Group> groups = groupService.projectEventList(events);
        List<Group> newGroups = new ArrayList<>();
        for (Group group : groups) {
            if (group.getMembers().contains(user)) {
                newGroups.add(group);
            }
        }
        return newGroups;
    }

    public Group getGroupById(Long groupId) throws EventException {
        List<Long> groupIds = new ArrayList<>();
        groupIds.add(groupId);
        List<Event> events = groupService.getGroupEvents(groupIds);
        return groupService.projectEventList(events).get(0);
    }
}
