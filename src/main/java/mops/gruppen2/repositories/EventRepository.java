package mops.gruppen2.repositories;

import mops.gruppen2.events.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
}
