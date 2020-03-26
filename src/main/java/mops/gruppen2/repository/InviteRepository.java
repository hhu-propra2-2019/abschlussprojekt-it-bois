package mops.gruppen2.repository;

import mops.gruppen2.domain.dto.InviteLinkDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface InviteRepository extends CrudRepository<InviteLinkDTO, Long> {

    @Query("")
    String ggg();
}
