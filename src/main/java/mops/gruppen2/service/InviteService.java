package mops.gruppen2.service;

import mops.gruppen2.domain.dto.InviteLinkDTO;
import mops.gruppen2.domain.exception.InvalidInviteException;
import mops.gruppen2.domain.exception.NoInviteExistException;
import mops.gruppen2.repository.InviteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InviteService {

    private static final Logger LOG = LoggerFactory.getLogger(InviteService.class);
    private final InviteRepository inviteRepository;

    public InviteService(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    void createLink(UUID groupId) {
        inviteRepository.save(new InviteLinkDTO(null, groupId.toString(), UUID.randomUUID().toString()));
    }

    void destroyLink(UUID groupId) {
        inviteRepository.deleteLinkOfGroup(groupId.toString());
    }

    public UUID getGroupIdFromLink(String link) {
        try {
            return UUID.fromString(inviteRepository.findGroupIdByLink(link));
        } catch (Exception e) {
            LOG.error("Gruppe zu Link ({}) konnte nicht gefunden werden!", link);
        }

        throw new InvalidInviteException(link);
    }

    public String getLinkByGroupId(UUID groupId) {
        try {
            return inviteRepository.findLinkByGroupId(groupId.toString());
        } catch (Exception e) {
            LOG.error("Link zu Gruppe ({}) konnte nicht gefunden werden!", groupId);
        }

        throw new NoInviteExistException(groupId.toString());
    }
}
