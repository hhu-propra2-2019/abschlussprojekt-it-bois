package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.event.*;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SerializationServiceTest {

    SerializationService serializationService;

    @BeforeEach
    public void setUp() {
        serializationService = new SerializationService(mock(EventRepository.class));
    }


    @Test
    void serializeEventTest() throws JsonProcessingException {
        Event event =  new Event(1L,"1");

        assertThat(serializationService.serializeEvent(event)).isEqualTo("{\"type\":\"Event\",\"group_id\":1,\"user_id\":\"1\"}");
    }
}
