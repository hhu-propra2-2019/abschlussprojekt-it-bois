package mops.gruppen2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SerializationServiceTest {


	@BeforeEach
	public void setUp() {
	}


	@Test
	void serializeEventTest() {
		Event event =  new Event(1L,1L,"1");

		SerializationService serializationService = new SerializationService(mock(EventRepository.class));

		try {
			assertThat(serializationService.serializeEvent(event)).isEqualTo("{\"type\":\"Event\",\"event_id\":1,\"group_id\":1,\"user_id\":\"1\"}");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Test
	void deserializeAddUserEvent() throws JsonProcessingException {
		SerializationService serializationService = new SerializationService(mock(EventRepository.class));

		String json = "{\"type\":\"Event\",\"event_id\":1,\"group_id\":1,\"user_id\":\"1\"}";

		Event event = serializationService.deserializeEvent(json);

		assertThat(event).isInstanceOf(Event.class);
	}

	@Test
	void serializeEventTestAddUserEvent(){
		AddUserEvent event = new AddUserEvent(1L,1L,"user_id","peter","mueller","a@a");
		SerializationService serializationService = new SerializationService(mock(EventRepository.class));
		try {
			assertThat(serializationService.serializeEvent(event)).isEqualTo("{\"type\":\"AddUserEvent\",\"event_id\":1,\"group_id\":1,\"user_id\":\"user_id\",\"givenname\":\"peter\",\"familyname\":\"mueller\",\"email\":\"a@a\"}");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
