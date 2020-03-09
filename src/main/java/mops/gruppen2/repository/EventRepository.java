package mops.gruppen2.repository;

import mops.gruppen2.domain.EventDTO;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<EventDTO, Long> {
}
