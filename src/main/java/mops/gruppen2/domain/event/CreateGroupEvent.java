package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor // For Jackson
public class CreateGroupEvent extends Event {

    private Visibility groupVisibility;
    private UUID groupParent;
    private GroupType groupType;
    private Long groupUserMaximum;

    public CreateGroupEvent(UUID groupId, String userId, UUID parent, GroupType type, Visibility visibility, Long userMaximum) {
        super(groupId, userId);
        this.groupParent = parent;
        this.groupType = type;
        this.groupVisibility = visibility;
        this.groupUserMaximum = userMaximum;
    }

    @Override
    protected void applyEvent(Group group) {
        group.setId(this.groupId);
        group.setParent(this.groupParent);
        group.setType(this.groupType);
        group.setVisibility(this.groupVisibility);
        group.setUserMaximum(this.groupUserMaximum);
    }
}
