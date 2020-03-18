package mops.gruppen2.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;

@Getter
@NoArgsConstructor // For Jackson
public class DeleteGroupEvent extends Event {

    public DeleteGroupEvent(Long groupId, String userId) {
        super(groupId, userId);
    }

    @Override
    public void apply(Group group) {
        group.getRoles().clear();
        group.getMembers().clear();
        group.setTitle(null);
        group.setDescription(null);
        group.setVisibility(null);
        group.setType(null);
        group.setParent(null);
    }
}
