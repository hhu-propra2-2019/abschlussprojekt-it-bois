package mops.gruppen2.service;

import mops.gruppen2.domain.dto.InviteLinkDTO;
import mops.gruppen2.domain.exception.InvalidInviteException;
import mops.gruppen2.domain.exception.NoInviteExistException;
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
        try {
            return UUID.fromString(inviteRepository.findGroupIdByLink(link));
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new InvalidInviteException(link);
    }

    public String getLinkByGroupId(UUID groupId) {
        try {
            return inviteRepository.findLinkByGroupId(groupId.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new NoInviteExistException(groupId.toString());
    }
}
