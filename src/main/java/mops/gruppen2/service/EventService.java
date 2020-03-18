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

    private final SerializationService serializationService;
    private final EventRepository eventStore;

    public EventService(SerializationService serializationService, EventRepository eventStore) {
        this.serializationService = serializationService;
        this.eventStore = eventStore;
    }

    /**
     * sichert ein Event Objekt indem es ein EventDTO Objekt erzeugt
     *
     * @param event
     */
    public void saveEvent(Event event) {
        EventDTO eventDTO = getDTO(event);
        eventStore.save(eventDTO);
    }

    /**
     * Erzeugt aus einem Event Objekt ein EventDTO Objekt.
     * Ist die Gruppe öffentlich, dann wird die visibility auf true gesetzt.
     *
     * @param event
     * @return EventDTO
     */
    public EventDTO getDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setGroup_id(event.getGroup_id());
        eventDTO.setUser_id(event.getUser_id());
        if (event instanceof CreateGroupEvent) {
            if (((CreateGroupEvent) event).getGroupVisibility() == Visibility.PRIVATE) {
                eventDTO.setVisibility(false);
            } else {
                eventDTO.setVisibility(true);
            }
        }

        try {
            eventDTO.setEvent_payload(serializationService.serializeEvent(event));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return eventDTO;
    }

    /**
     * Gibt die nächst höhere groupID zurück die belegt werden kann.
     * Gibt 1 zurück, falls keine Gruppe vorhanden ist.
     *
     * @return Gibt Long zurück
     */
    public Long checkGroup() {
        Long maxGroupID = eventStore.getMaxGroupID();
        if (maxGroupID == null) {
            return 1L;
        }
        return maxGroupID + 1;
    }

    /**
     * Findet alle Events welche ab dem neuen Status hinzugekommen sind
     *
     * @param status
     * @return Liste von Events
     */
    public List<Event> getNewEvents(Long status) {
        List<Long> groupIdsThatChanged = eventStore.findNewEventSinceStatus(status);

        List<EventDTO> groupEventDTOS = eventStore.findAllEventsOfGroups(groupIdsThatChanged);
        return translateEventDTOs(groupEventDTOS);
    }

    /**
     * Erzeugt aus einer Liste von eventDTOs eine Liste von Events
     *
     * @param eventDTOS
     * @return Liste von Events
     */
    public List<Event> translateEventDTOs(Iterable<EventDTO> eventDTOS) {
        List<Event> events = new ArrayList<>();

        for (EventDTO eventDTO : eventDTOS) {
            try {
                events.add(serializationService.deserializeEvent(eventDTO.getEvent_payload()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return events;
    }

    /**
     * Sichert eine Liste von Event Objekten mithilfe der Methode saveEvent(Event event)
     *
     * @param createGroupEvents Liste von Event Objekten
     */
    public void saveEventList(List<Event> createGroupEvents) {
        for (Event event : createGroupEvents) {
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
