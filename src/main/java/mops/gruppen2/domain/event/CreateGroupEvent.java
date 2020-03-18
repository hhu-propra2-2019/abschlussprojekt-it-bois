package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupEvent extends Event {
    private Visibility groupVisibility;
    private Long groupParent;
    private GroupType groupType;

    public CreateGroupEvent(Long group_id, String user_id, Long parent, GroupType type, Visibility visibility) {
        super(group_id, user_id);
        this.groupParent = parent;
        this.groupType = type;
        this.groupVisibility = visibility;
    }

    @Override
    public void applyEvent(Group group) {
        group.setId(this.group_id);
        group.setParent(this.groupParent);
        group.setType(this.groupType);
        group.setVisibility(this.groupVisibility);
    }
}
