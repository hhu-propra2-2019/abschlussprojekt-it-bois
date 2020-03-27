package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.GroupNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Hallo
@Service
public class UserService {

    private final GroupService groupService;
    private final EventService eventService;

    public UserService(GroupService groupService, EventService eventService) {
        this.groupService = groupService;
        this.eventService = eventService;
    }

    //Test nötig??

    @Cacheable("groups")
    public List<Group> getUserGroups(User user) throws EventException {
        List<UUID> groupIds = eventService.findGroupIdsByUser(user.getId());
        List<Event> events = groupService.getGroupEvents(groupIds);
        List<Group> groups = groupService.projectEventList(events);
        List<Group> newGroups = new ArrayList<>();
        for (Group group : groups) {
            if (group.getMembers().contains(user)) {
                newGroups.add(group);
            }
        }

        groupService.sortByGroupType(newGroups);

        return newGroups;
    }

    @Cacheable("groups")
    public List<Group> getUserGroups(String userId) throws EventException {
        return getUserGroups(new User(userId, null, null, null));
    }

    public Group getGroupById(UUID groupId) throws EventException {
        List<UUID> groupIds = new ArrayList<>();
        groupIds.add(groupId);

        try {
            List<Event> events = groupService.getGroupEvents(groupIds);
            return groupService.projectEventList(events).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new GroupNotFoundException("@UserService");
        }
    }
}
