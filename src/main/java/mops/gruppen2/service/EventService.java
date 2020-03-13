package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
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

    /** sichert ein Event Objekt indem es ein EventDTO Objekt erzeugt
     *
     * @param event
     */
    public void saveEvent(Event event){
        EventDTO eventDTO = getDTO(event);
        eventStore.save(eventDTO);
    }

    /** Erzeugt aus einem Event Objekt ein EventDTO Objekt
     *
     * @param event
     * @return EventDTO
     */
    public EventDTO getDTO(Event event){
        EventDTO eventDTO = new EventDTO();
        eventDTO.setGroup_id(event.getGroup_id());
        eventDTO.setUser_id(event.getUser_id());
        try {
            eventDTO.setEvent_payload(serializationService.serializeEvent(event));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return  eventDTO;
    }

    /** Sorgt dafür die Group_id immer um 1 zu erhöhen
     *
     * @return Gibt Long zurück
     */
    public Long checkGroup() {
        Long tmpId = 1L;
        Iterable<EventDTO> eventDTOS = eventStore.findAll();
        for (EventDTO event : eventDTOS) {
            if (event.getGroup_id() == null) {
                return tmpId;
            }
            if (tmpId <= event.getGroup_id()) {
                tmpId++;
            }
        }
        return tmpId;
    }

    /** Findet alle Events welche ab dem neuen Status hinzugekommen sind
     *
     * @param status
     * @return Liste von Events
     */
    public List<Event> getNewEvents(Long status){
        Iterable<EventDTO> eventDTOS = eventStore.findNewEventSinceStatus(status);

        return translateEventDTOs(eventDTOS);
    }

    /** Erzeugt aus der Datenbank eine Liste von Events
     *
     * @param eventDTOS
     * @return Liste von Events
     */
    public List<Event> translateEventDTOs(Iterable<EventDTO> eventDTOS){
        List<Event> events = new ArrayList<>();

        for (EventDTO eventDTO : eventDTOS) {
            try {
                events.add(serializationService.deserializeEvent(eventDTO.getEvent_payload()));

            }catch (JsonProcessingException e) {
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
        for(Event event : createGroupEvents) {
            saveEvent(event);
        }
    }

}
