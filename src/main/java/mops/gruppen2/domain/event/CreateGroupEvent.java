package mops.gruppen2.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;

import java.util.UUID;

@Getter
@NoArgsConstructor // For Jackson
public class CreateGroupEvent extends Event {

    private Visibility groupVisibility;
    private UUID groupParent;
    private GroupType groupType;
    private Long groupUserMaximum;

    public CreateGroupEvent(UUID groupId, String userId, UUID parent, GroupType type, Visibility visibility, Long userMaximum) {
        super(groupId, userId);
        groupParent = parent;
        groupType = type;
        groupVisibility = visibility;
        groupUserMaximum = userMaximum;
    }

    @Override
    protected void applyEvent(Group group) {
        group.setId(groupId);
        group.setParent(groupParent);
        group.setType(groupType);
        group.setVisibility(groupVisibility);
        group.setUserMaximum(groupUserMaximum);
    }
}
