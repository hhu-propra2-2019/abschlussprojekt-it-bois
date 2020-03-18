package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * Übersetzt mithilfe der Jackson-Library eine Java-Event-Repräsentation zu einem JSON-Event-Payload.
     *
     * @param event Java-Event-Repräsentation
     * @return JSON-Event-Payload als String
     * @throws JsonProcessingException
     */

    public String serializeEvent(Event event) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(event);
    }

    /**
     * Übersetzt mithilfe der Jackson-Library einen JSON-Event-Payload zu einer Java-Event-Repräsentation.
     *
     * @param json JSON-Event-Payload als String
     * @return Java-Event-Repräsentation
     * @throws JsonProcessingException
     */
    public Event deserializeEvent(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Event.class);
    }
}
