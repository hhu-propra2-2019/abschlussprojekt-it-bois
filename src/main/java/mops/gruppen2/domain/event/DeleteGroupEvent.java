package mops.gruppen2.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;

@Getter
@NoArgsConstructor // For Jackson
public class DeleteGroupEvent extends Event {

    public DeleteGroupEvent(long group_id, String user_id) {
        super(group_id, user_id);
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
