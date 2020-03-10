package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.event.*;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
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
        Event event =  new Event(1L,1L,"1");

        assertThat(serializationService.serializeEvent(event)).isEqualTo("{\"type\":\"Event\",\"event_id\":1,\"group_id\":1,\"user_id\":\"1\"}");
    }

    @Test
    void deserializeAddUserEventToRightClass() throws JsonProcessingException {
        String json = "{\"type\":\"AddUserEvent\",\"event_id\":1,\"group_id\":1,\"user_id\":\"1\"}";

        Event event = serializationService.deserializeEvent(json);

        assertThat(event).isInstanceOf(AddUserEvent.class);
    }

    @Test
    void deserializeDeleteUserEventToRightClass() throws JsonProcessingException {
        String json = "{\"type\":\"DeleteUserEvent\",\"event_id\":1,\"group_id\":1,\"user_id\":\"1\"}";

        Event event = serializationService.deserializeEvent(json);

        assertThat(event).isInstanceOf(DeleteUserEvent.class);
    }

    @Test
    void deserializeUpdateGroupDescriptionEventToRightClass() throws JsonProcessingException {
        String json = "{\"type\":\"UpdateGroupDescriptionEvent\",\"event_id\":1,\"group_id\":1,\"user_id\":\"1\",\"newGroupDescription\":\"test\"}";

        Event event = serializationService.deserializeEvent(json);

        assertThat(event).isInstanceOf(UpdateGroupDescriptionEvent.class);
    }

    @Test
    void deserializeUpdateGroupTitleEventToRightClass() throws JsonProcessingException {
        String json = "{\"type\":\"UpdateGroupTitleEvent\",\"event_id\":1,\"group_id\":1,\"user_id\":\"1\",\"newGroupTitle\":\"test\"}";

        Event event = serializationService.deserializeEvent(json);

        assertThat(event).isInstanceOf(UpdateGroupTitleEvent.class);
    }

    @Test
    void deserializeUpdateRoleEventToRightClass() throws JsonProcessingException {
	    System.out.println(serializationService.serializeEvent(new UpdateRoleEvent(1L, 1L, "1", Role.ADMIN)));

	    String json = "{\"type\":\"UpdateRoleEvent\",\"event_id\":1,\"group_id\":1,\"user_id\":1,\"newRole\":\"ADMIN\"}";

        Event event = serializationService.deserializeEvent(json);

        assertThat(event).isInstanceOf(UpdateRoleEvent.class);
    }

    @Test
    void deserializeCreateGroupEventToRightClass() throws JsonProcessingException {
        String json = "{\"type\":\"CreateGroupEvent\",\"event_id\":1,\"group_id\":1,\"user_id\":\"1\",\"groupTitle\":\"test\",\"groupDescription\":\"test\"}";

        Event event = serializationService.deserializeEvent(json);

        assertThat(event).isInstanceOf(CreateGroupEvent.class);
    }
}
