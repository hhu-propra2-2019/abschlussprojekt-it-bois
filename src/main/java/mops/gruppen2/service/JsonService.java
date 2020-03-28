package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mops.gruppen2.domain.event.Event;
import org.springframework.stereotype.Service;

/**
 * Übersetzt JSON-Event-Payloads zu Java-Event-Repräsentationen und zurück.
 */
@Service
public final class JsonService {

    private JsonService() {}

    /**
     * Übersetzt mithilfe der Jackson-Library eine Java-Event-Repräsentation zu einem JSON-Event-Payload.
     *
     * @param event Java-Event-Repräsentation
     *
     * @return JSON-Event-Payload als String
     *
     * @throws JsonProcessingException Bei JSON Fehler
     */

    static String serializeEvent(Event event) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(event);
    }

    /**
     * Übersetzt mithilfe der Jackson-Library einen JSON-Event-Payload zu einer Java-Event-Repräsentation.
     *
     * @param json JSON-Event-Payload als String
     *
     * @return Java-Event-Repräsentation
     *
     * @throws JsonProcessingException Bei JSON Fehler
     */
    static Event deserializeEvent(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Event.class);
    }
}
