package mops.gruppen2.repository;

import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.domain.event.Event;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<EventDTO, Long> {
    @Query("SELECT * FROM event WHERE event_id > ?#{[0]}")
    public Iterable<EventDTO> findNewEventSinceStatus(@Param("status") Long status);
}
