package mops.gruppen2.repository;

import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.domain.event.Event;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<EventDTO, Long> {
    @Query("select distinct group_id from event where user_id =:id")
    List<Long> findGroup_idsWhereUser_id(@Param("id") String user_id);

    @Query("select * from event where group_id =:id")
    List<EventDTO> findEventDTOByGroup_id(@Param("id") Long group_id);

    @Query("SELECT * FROM event WHERE event_id > ?#{[0]}")
    Iterable<EventDTO> findNewEventSinceStatus(@Param("status") Long status);

   @Query("select * from event where visibility =:vis")
    List<EventDTO> findEventDTOByVisibility(@Param("vis") Boolean visibility);
}
