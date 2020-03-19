package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.repository.EventRepository;
import mops.gruppen2.security.Account;
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

    /**
     * Sucht in der DB alle Zeilen raus welche eine der Gruppen_ids hat.
     * Wandelt die Zeilen in Events um und gibt davon eine Liste zurück.
     *
     * @param groupIds Liste an IDs
     * @return Liste an Events
     */
    public List<Event> getGroupEvents(List<Long> groupIds) {
        List<EventDTO> eventDTOS = new ArrayList<>();
        for (Long groupId : groupIds) {
            eventDTOS.addAll(eventRepository.findEventDTOByGroup_id(groupId));
        }
        return eventService.translateEventDTOs(eventDTOS);
    }

    /**
     * Erzeugt eine neue Map wo Gruppen aus den Events erzeugt und den Gruppen_ids zugeordnet werden.
     * Die Gruppen werden als Liste zurückgegeben
     *
     * @param events Liste an Events
     * @return Liste an Projizierten Gruppen
     * @throws EventException Projektionsfehler
     */
    public List<Group> projectEventList(List<Event> events) throws EventException {
        Map<Long, Group> groupMap = new HashMap<>();

        for (Event event : events) {
            Group group = getOrCreateGroup(groupMap, event.getGroupId());
            event.apply(group);
        }

        return new ArrayList<>(groupMap.values());
    }

    private Group getOrCreateGroup(Map<Long, Group> groups, long groupId) {
        if (!groups.containsKey(groupId)) {
            groups.put(groupId, new Group());
        }

        return groups.get(groupId);
    }

    private List<Long> removeUserGroups(List<Long> groupIds, List<Long> userGroups) {
        for (Long groupId : userGroups) {
            groupIds.remove(groupId);
        }
        return groupIds;
    }

    /**
     * Sucht alle Zeilen in der DB mit visibility=true.
     * Erstellt eine Liste aus öffentlichen Gruppen (ohen bereits beigetretenen Gruppen).
     *
     * @return Liste von projizierten Gruppen
     * @throws EventException Projektionsfehler
     */
    public List<Group> getAllGroupWithVisibilityPublic(String userId) throws EventException {
        List<Long> groupIds = removeUserGroups(eventRepository.findGroup_idsWhereVisibility(Boolean.TRUE), eventRepository.findGroup_idsWhereUser_id(userId));
        List<EventDTO> eventDTOS = eventRepository.findAllEventsOfGroups(groupIds);
        List<Event> events = eventService.translateEventDTOs(eventDTOS);
        return projectEventList(events);
    }


    /**
     * Filtert alle öffentliche Gruppen nach dem Suchbegriff und gibt diese als Liste von Gruppen zurück.
     * Groß und Kleinschreibung wird nicht beachtet.
     *
     * @param search Der Suchstring
     * @return Liste von projizierten Gruppen
     * @throws EventException Projektionsfehler
     */
    public List<Group> findGroupWith(String search, Account account) throws EventException {
        List<Group> groups = new ArrayList<>();
        for (Group group : getAllGroupWithVisibilityPublic(account.getName())) {
            if (group.getTitle().toLowerCase().contains(search.toLowerCase()) || group.getDescription().toLowerCase().contains(search.toLowerCase())) {
                groups.add(group);
            }
        }
        return groups;
    }

}
