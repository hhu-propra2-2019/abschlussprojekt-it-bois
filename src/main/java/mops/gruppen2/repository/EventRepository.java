package mops.gruppen2.repository;

import mops.gruppen2.domain.dto.EventDTO;
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

    //@Query("SELECT * FROM event WHERE event_id > ?#{[0]}")
    //Iterable<EventDTO> findNewEventSinceStatus(@Param("status") Long status);

   @Query("select distinct group_id from event where visibility =:vis")
   List<Long> findGroup_idsWhereVisibility(@Param("vis") Boolean visibility);
  
    @Query("SELECT DISTINCT group_id FROM event WHERE event_id > :status")
    public List<Long> findNewEventSinceStatus(@Param("status") Long status);

    @Query("SELECT * FROM event WHERE group_id IN (:groupIds) ")
    public List<EventDTO> findAllEventsOfGroups(@Param("groupIds") List<Long> groupIds);

    @Query("SELECT MAX(event_id) FROM event")
    public Long getHighesEvent_ID();
}
