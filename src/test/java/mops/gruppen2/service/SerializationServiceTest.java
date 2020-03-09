package mops.gruppen2.service;

import mops.gruppen2.domain.event.Event;
import mops.gruppen2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SerializationServiceTest {

	EventRepository eventRepository;

	@BeforeEach
	public void setUp(){
	}


	@Disabled
	@Test
	void applyEvent() {
	}

	@Test
	void serializeEventTest(){
		Event event = new Event(1,1,"1");
		SerializationService serializationService = new SerializationService(eventRepository);

	}

}