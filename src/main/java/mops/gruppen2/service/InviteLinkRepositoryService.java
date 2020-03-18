package mops.gruppen2.service;

import mops.gruppen2.domain.dto.InviteLinkDTO;
import mops.gruppen2.repository.InviteLinkRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InviteLinkRepositoryService {

    private final InviteLinkRepository inviteLinkRepository;

    public InviteLinkRepositoryService(InviteLinkRepository inviteLinkRepository) {
        this.inviteLinkRepository = inviteLinkRepository;
    }

    public long findGroupIdByInvite(String link) {
        return inviteLinkRepository.findGroupIdByLink(link);
    }

    public void saveInvite(Long group_id, UUID link) {
        inviteLinkRepository.save(new InviteLinkDTO(null, group_id, link.toString()));
    }

}
