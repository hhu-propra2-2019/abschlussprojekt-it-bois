package mops.gruppen2.repository;

import mops.gruppen2.domain.EventDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<EventDTO, Long> {
    @Query("SELECT * FROM event WHERE event_id > :status")
    public Iterable<EventDTO> findNewEventSinceStatus(@Param("status") Long status);

    @Query("SELECT * FROM event WHERE group_id IN (:groupIds) ")
    public Iterable<EventDTO> findAllEventsOfGroups(@Param("groupIds") List<Long> groupIds);

    @Query("SELECT MAX(event_id) FROM event")
    public Long getHighesEvent_ID();
}
