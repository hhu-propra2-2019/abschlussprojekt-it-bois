package mops.gruppen2.service;

import mops.gruppen2.domain.dto.InviteLinkDTO;
import mops.gruppen2.repository.InviteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InviteService {

    private final InviteRepository inviteRepository;

    public InviteService(InviteRepository inviteRepository) {this.inviteRepository = inviteRepository;}

    public void createLink(UUID groupId) {
        inviteRepository.save(new InviteLinkDTO(null, groupId.toString(), UUID.randomUUID().toString()));
    }

    public void destroyLink(UUID groupId) {
        inviteRepository.deleteLinkOfGroup(groupId.toString());
    }

    public UUID getGroupIdFromLink(String link) {
        return UUID.fromString(inviteRepository.findGroupIdByLink(link));
    }

    public String getLinkFromGroupId(UUID groupId) {
        return inviteRepository.findLinkByGroupId(groupId.toString());
    }

}
