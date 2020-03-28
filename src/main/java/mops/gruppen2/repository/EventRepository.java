package mops.gruppen2.repository;

import mops.gruppen2.domain.dto.EventDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<EventDTO, Long> {

    @Query("SELECT distinct group_id FROM event WHERE user_id =:id AND event_type = :type")
    List<String> findGroupIdsWhereUserId(@Param("id") String userId, @Param("type") String type);

    @Query("SELECT * from event WHERE group_id =:id")
    List<EventDTO> findEventDTOByGroupId(@Param("id") String groupId);

    @Query("SELECT DISTINCT group_id FROM event WHERE event_id > :status")
    List<String> findNewEventSinceStatus(@Param("status") Long status);

    @Query("SELECT * FROM event WHERE group_id IN (:groupIds) ")
    List<EventDTO> findAllEventsOfGroups(@Param("groupIds") List<String> groupIds);

    @Query("SELECT MAX(event_id) FROM event")
    Long getHighesEventID();

    @Query("SELECT * FROM event WHERE event_type = :type")
    List<EventDTO> findAllEventsByType(@Param("type") String type);

    @Query("SELECT * FROM event WHERE event_type = :type AND user_id = :userId")
    List<EventDTO> findEventsByTypeAndUserId(@Param("type") String type, @Param("userId") String userId);

    @Query("SELECT COUNT(*) FROM event WHERE event_type = :type AND group_id = :groupId")
    Long countEventsByTypeAndGroupId(@Param("type") String type, @Param("groupId") String groupId);

    @Query("SELECT COUNT(*) FROM event WHERE group_id = :groupId AND user_id = :userId AND event_type = :type")
    Long countEventsByGroupIdAndUserIdAndEventType(@Param("groupId") String groupId, @Param("userId") String userId, @Param("type") String type);
}
