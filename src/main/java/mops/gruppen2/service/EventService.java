package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final JsonService jsonService;
    private final EventRepository eventStore;

    public EventService(JsonService jsonService, EventRepository eventStore) {
        this.jsonService = jsonService;
        this.eventStore = eventStore;
    }

    /**
     * Erzeugt ein DTO aus einem Event und speicher es.
     *
     * @param event Event, welches gespeichert wird
     */
    public void saveEvent(Event event) {
        EventDTO eventDTO = getDTO(event);
        eventStore.save(eventDTO);
    }

    /**
     * Erzeugt aus einem Event Objekt ein EventDTO Objekt.
     * Ist die Gruppe öffentlich, dann wird die visibility auf true gesetzt.
     *
     * @param event Event, welches in DTO übersetzt wird
     * @return EventDTO Neues DTO
     */
    public EventDTO getDTO(Event event) {
        boolean visibility = false;
        if (event instanceof CreateGroupEvent) {
            visibility = ((CreateGroupEvent) event).getGroupVisibility() == Visibility.PUBLIC;
        }

        String payload = "";
        try {
            payload = jsonService.serializeEvent(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new EventDTO(null, event.getGroupId(), event.getUserId(), payload, visibility);
    }

    /**
     * Gibt die nächst höhere groupID zurück die belegt werden kann.
     * Gibt 1 zurück, falls keine Gruppe vorhanden ist.
     *
     * @return Long GruppenId
     */
    public Long checkGroup() {
        Long maxGroupID = eventStore.getMaxGroupID();
        if (maxGroupID == null) {
            return 1L;
        }
        return maxGroupID + 1;
    }

    /**
     * Findet alle Events welche ab dem neuen Status hinzugekommen sind.
     *
     * @param status Die Id des zuletzt gespeicherten Events
     * @return Liste von neueren Events
     */
    public List<Event> getNewEvents(Long status) {
        List<Long> groupIdsThatChanged = eventStore.findNewEventSinceStatus(status);

        List<EventDTO> groupEventDTOS = eventStore.findAllEventsOfGroups(groupIdsThatChanged);
        return translateEventDTOs(groupEventDTOS);
    }

    /**
     * Erzeugt aus einer Liste von eventDTOs eine Liste von Events.
     *
     * @param eventDTOS Liste von DTOs
     * @return Liste von Events
     */
    public List<Event> translateEventDTOs(Iterable<EventDTO> eventDTOS) {
        List<Event> events = new ArrayList<>();

        for (EventDTO eventDTO : eventDTOS) {
            try {
                events.add(jsonService.deserializeEvent(eventDTO.getEvent_payload()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return events;
    }

    /**
     * Sichert eine Liste von Event Objekten mithilfe der Methode saveEvent(Event event).
     *
     * @param eventList Liste von Event Objekten
     */
    public void saveEventList(List<Event> eventList) {
        for (Event event : eventList) {
            saveEvent(event);
        }
    }

    public Long getMaxEvent_id() {
        return eventStore.getHighesEvent_ID();
    }

    public List<Long> getGroupsOfUser(String userID) {
        return eventStore.findGroup_idsWhereUser_id(userID);
    }

    public List<Event> getEventsOfGroup(Long groupId) {
        List<EventDTO> eventDTOList = eventStore.findEventDTOByGroup_id(groupId);
        return translateEventDTOs(eventDTOList);
    }

}
