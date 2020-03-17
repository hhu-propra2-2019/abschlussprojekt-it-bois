package mops.gruppen2.repository;

import mops.gruppen2.domain.dto.InviteLinkDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteLinkRepository extends CrudRepository<InviteLinkDTO, String> {
    @Query("select invite_link from inviteLink where group_id =:id")
    String findLinkByGroupID(@Param("id") Long GroupID);
}
