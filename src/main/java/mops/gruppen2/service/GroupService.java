package mops.gruppen2.service;

import mops.gruppen2.domain.Account;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.repository.EventRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
     *
     * @return Liste an Events
     */
    //TODO: Das vielleicht in den EventRepoService?
    public List<Event> getGroupEvents(List<UUID> groupIds) {
        List<EventDTO> eventDTOS = new ArrayList<>();
        for (UUID groupId : groupIds) {
            eventDTOS.addAll(eventRepository.findEventDTOByGroupId(groupId.toString()));
        }
        return eventService.getEventsFromDTOs(eventDTOS);
    }

    /**
     * Wird verwendet beim Gruppe erstellen bei der Parent-Auswahl: nur Titel benötigt.
     *
     * @return List of groups
     */
    @Cacheable("groups")
    public List<Group> getAllLecturesWithVisibilityPublic() {
        List<Event> createEvents = eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("CreateGroupEvent"));
        createEvents.addAll(eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("DeleteGroupEvent")));
        createEvents.addAll(eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("UpdateGroupTitleEvent")));
        createEvents.addAll(eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("DeleteGroupEvent")));

        List<Group> visibleGroups = projectEventList(createEvents);

        return visibleGroups.stream()
                            .filter(group -> group.getType() == GroupType.LECTURE)
                            .filter(group -> group.getVisibility() == Visibility.PUBLIC)
                            .collect(Collectors.toList());
    }

    /**
     * Erzeugt eine neue Map wo Gruppen aus den Events erzeugt und den Gruppen_ids zugeordnet werden.
     * Die Gruppen werden als Liste zurückgegeben.
     *
     * @param events Liste an Events
     *
     * @return Liste an Projizierten Gruppen
     *
     * @throws EventException Projektionsfehler
     */
    public List<Group> projectEventList(List<Event> events) throws EventException {
        Map<UUID, Group> groupMap = new HashMap<>();

        events.parallelStream()
              .forEachOrdered(event -> event.apply(getOrCreateGroup(groupMap, event.getGroupId())));

        return new ArrayList<>(groupMap.values());
    }

    /**
     * Gibt die Gruppe mit der richtigen Id aus der übergebenen Map wieder, existiert diese nicht
     * wird die Gruppe erstellt und der Map hizugefügt.
     *
     * @param groups  Map aus GruppenIds und Gruppen
     * @param groupId Die Id der Gruppe, die zurückgegeben werden soll
     *
     * @return Die gesuchte Gruppe
     */
    private static Group getOrCreateGroup(Map<UUID, Group> groups, UUID groupId) {
        if (!groups.containsKey(groupId)) {
            groups.put(groupId, new Group());
        }

        return groups.get(groupId);
    }

    /**
     * Filtert alle öffentliche Gruppen nach dem Suchbegriff und gibt diese als Liste von Gruppen zurück.
     * Groß und Kleinschreibung wird nicht beachtet.
     *
     * @param search Der Suchstring
     *
     * @return Liste von projizierten Gruppen
     *
     * @throws EventException Projektionsfehler
     */
    //Todo Rename
    @Cacheable("groups")
    public List<Group> findGroupWith(String search, Account account) throws EventException {
        if (search.isEmpty()) {
            return getAllGroupWithVisibilityPublic(account.getName());
        }

        return getAllGroupWithVisibilityPublic(account.getName()).parallelStream().filter(group -> group.getTitle().toLowerCase().contains(search.toLowerCase()) || group.getDescription().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
    }

    /**
     * Wird verwendet bei der Suche nach Gruppen: Titel, Beschreibung werden benötigt.
     * Außerdem wird beachtet, ob der eingeloggte User bereits in entsprechenden Gruppen mitglied ist.
     *
     * @return Liste von projizierten Gruppen
     *
     * @throws EventException Projektionsfehler
     */
    //TODO Rename
    @Cacheable("groups")
    public List<Group> getAllGroupWithVisibilityPublic(String userId) throws EventException {
        List<Event> groupEvents = eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("CreateGroupEvent"));
        groupEvents.addAll(eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("UpdateGroupDescriptionEvent")));
        groupEvents.addAll(eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("UpdateGroupTitleEvent")));
        groupEvents.addAll(eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("DeleteGroupEvent")));
        groupEvents.addAll(eventService.getEventsFromDTOs(eventRepository.findAllEventsByType("UpdateUserMaxEvent")));

        List<Group> visibleGroups = projectEventList(groupEvents);

        sortByGroupType(visibleGroups);

        return visibleGroups.stream()
                            .filter(group -> group.getType() != null)
                            .filter(group -> !eventService.userInGroup(group.getId(), userId))
                            .filter(group -> group.getVisibility() == Visibility.PUBLIC)
                            .collect(Collectors.toList());
    }

    /**
     * Sortiert die übergebene Liste an Gruppen, sodass Veranstaltungen am Anfang der Liste sind.
     *
     * @param groups Die Liste von Gruppen die sortiert werden soll
     */
    void sortByGroupType(List<Group> groups) {
        groups.sort((Group g1, Group g2) -> {
            if (g1.getType() == GroupType.LECTURE) {
                return -1;
            }
            if (g2.getType() == GroupType.LECTURE) {
                return 0;
            }

            return 1;
        });
    }
}
