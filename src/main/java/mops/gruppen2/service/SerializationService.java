package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Übersetzt JSON-Event-Payloads zu Java-Event-Repräsentationen und zurück.
 */
@Service
public class SerializationService {

    private final EventRepository eventStore;
    private final Logger log = LoggerFactory.getLogger(SerializationService.class);

    public SerializationService(EventRepository eventStore) {
        this.eventStore = eventStore;
    }

    public String serializeEvent(Event event) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json =  mapper.writeValueAsString(event);
        log.info(json);
        return json;
    }


}
