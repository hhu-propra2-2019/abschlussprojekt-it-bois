package mops.gruppen2.repository;

import mops.gruppen2.domain.dto.InviteLinkDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface InviteRepository extends CrudRepository<InviteLinkDTO, Long> {

    @Query("SELECT group_id FROM invite WHERE invite_link = :link")
    String findGroupIdByLink(@Param("link") String link);

    @Query("DELETE FROM invite WHERE group_id = :group")
    void deleteLinkOfGroup(@Param("group") String group);

    @Query("SELECT invite_link FROM invite WHERE group_id = :group")
    String findLinkByGroupId(@Param("group") String groupId);
}
