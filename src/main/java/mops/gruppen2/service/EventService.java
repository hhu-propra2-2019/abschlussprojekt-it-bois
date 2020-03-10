package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final SerializationService serializationService;
    private final EventRepository eventStore;

    public EventService(SerializationService serializationService, EventRepository eventStore) {
        this.serializationService = serializationService;
        this.eventStore = eventStore;
    }


    public void saveEvent(Event event){
        EventDTO eventDTO = getDTO(event);
        eventStore.save(eventDTO);

    }

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

    public List<EventDTO> findAllEvents() {
        return null;
    }

    public Event getEvent(EventDTO eventDTO) {
        try {
            return serializationService.deserializeEvent(eventDTO.getEvent_payload());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
