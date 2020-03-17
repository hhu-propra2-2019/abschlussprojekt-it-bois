package mops.gruppen2.repository;

import mops.gruppen2.domain.dto.InviteLinkDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteLinkRepository extends CrudRepository<InviteLinkDTO, Long> {

    //@Query("SELECT invite_link FROM invite WHERE group_id = :id")
    //String findLinkByGroupID(@Param("id") Long GroupID);

    @Query("SELECT group_id FROM invite WHERE invite_link = :link")
    Long findGroupIdByLink(@Param("link") String link);
}
