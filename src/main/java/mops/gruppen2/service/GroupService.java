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

    /** Sucht in der DB alle Zeilen raus welche eine der Gruppen_ids hat.
     * Wandelt die Zeilen in Events um und gibt davon eine Liste zurück.
     *
     * @param group_ids
     * @return
     */
    public List<Event> getGroupEvents(List<Long> group_ids) {
        List<EventDTO> eventDTOS = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        for (Long group_id: group_ids) {
            eventDTOS.addAll(eventRepository.findEventDTOByGroup_id(group_id));
        }
        return events = eventService.translateEventDTOs(eventDTOS);
    }

    /** Erzeugt eine neue Map wo Gruppen aus den Events erzeugt und den Gruppen_ids zugeordnet werden.
     *  Die Gruppen werden als Liste zurückgegeben
     *
     * @param events
     * @return
     * @throws EventException
     */
    public List<Group> projectEventList(List<Event> events) throws EventException {
        Map<Long, Group> groupMap = new HashMap<>();

        for (Event event : events) {
            getOrCreateGroup(groupMap, event.getGroup_id()).applyEvent(event);
        }

        return new ArrayList<>(groupMap.values());
    }

    /** guckt in der Map anhand der Id nach ob die Gruppe schon in der Map vorhanden ist, wenn nicht wird eine neue
     *  Gruppe erzeugt
     *
     * @param groups
     * @param group_id
     * @return
     */
    private Group getOrCreateGroup(Map<Long, Group> groups, long group_id) {
        if (!groups.containsKey(group_id)) {
            groups.put(group_id, new Group());
        }

        return groups.get(group_id);
    }

    /**
     * sucht alle Zeilen in der DB wo die Visibility gleich true ist und wandelt diese in
     * eine Liste von Gruppen
     * @return
     * @throws EventException
     */
    public List<Group> getAllGroupWithVisibilityPublic() throws EventException {
        return projectEventList(eventService.translateEventDTOs(eventRepository.findEventDTOByVisibility(Boolean.FALSE)));
    }


    /**
     * Filtert alle öffentliche Gruppen nach dem suchbegriff und gibt diese als Liste von Gruppen zurück.
     * Groß und kleinschreibung wird beachtet.
     * @param search
     * @return
     * @throws EventException
     */
    public List<Group> findGroupWith(String search) throws EventException {
        List<Group> groups = new ArrayList<>();
        for (Group group: getAllGroupWithVisibilityPublic()) {
            if (group.getTitle().contains(search)){
                groups.add(group);
            }
        }
        return groups;
    }

}
