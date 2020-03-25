package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mops.gruppen2.domain.dto.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        String payload = "";
        try {
            payload = jsonService.serializeEvent(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new EventDTO(null, event.getGroupId().toString(), event.getUserId(), getEventType(event), payload);
    }

    private String getEventType(Event event) {
        int lastDot = event.getClass().getName().lastIndexOf('.');

        return event.getClass().getName().substring(lastDot + 1);
    }

    /**
     * Gibt die nächst höhere groupID zurück die belegt werden kann.
     * Gibt 1 zurück, falls keine Gruppe vorhanden ist.
     *
     * @return Long GruppenId
     */
    public UUID checkGroup() {
        return UUID.randomUUID();

        /*Long maxGroupID = eventStore.getMaxGroupID();

        if (maxGroupID == null) {
            return 1L;
        }

        return maxGroupID + 1;*/
    }

    /**
     * Findet alle Events welche ab dem neuen Status hinzugekommen sind.
     *
     * @param status Die Id des zuletzt gespeicherten Events
     * @return Liste von neueren Events
     */
    public List<Event> getNewEvents(Long status) {
        List<String> groupIdsThatChanged = eventStore.findNewEventSinceStatus(status);

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

    public List<Event> getEventsOfGroup(UUID groupId) {
        List<EventDTO> eventDTOList = eventStore.findEventDTOByGroup_id(groupId.toString());
        return translateEventDTOs(eventDTOList);
    }

    public List<UUID> findGroupIdsByUser(String userId) {
        return eventStore.findGroup_idsWhereUser_id(userId).stream()
                         .map(UUID::fromString)
                         .collect(Collectors.toList());
    }

}
