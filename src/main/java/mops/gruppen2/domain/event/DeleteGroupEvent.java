package mops.gruppen2.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;

import java.util.UUID;

@Getter
@NoArgsConstructor // For Jackson
public class DeleteGroupEvent extends Event {

    public DeleteGroupEvent(UUID groupId, String userId) {
        super(groupId, userId);
    }

    @Override
    protected void applyEvent(Group group) {
        group.getRoles().clear();
        group.getMembers().clear();
        group.setTitle(null);
        group.setDescription(null);
        group.setVisibility(null);
        group.setType(null);
        group.setParent(null);
        group.setUserMaximum(0L);
    }
}
