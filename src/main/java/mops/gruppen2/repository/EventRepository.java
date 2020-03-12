package mops.gruppen2.repository;

import mops.gruppen2.domain.EventDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<EventDTO, Long> {
    @Query("select distinct group_id where user_id =:id")
    List<Long> findGroup_idsWhereUser_id(@Param("id") Long user_id);

    @Query("select * where group_id =:id")
    List<EventDTO> findEventDTOByGroup_id(@Param("id") Long group_id);
}
