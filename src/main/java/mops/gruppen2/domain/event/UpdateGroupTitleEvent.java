package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;

/**
 * Ändert nur den Gruppentitel.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor // For Jackson
public class UpdateGroupTitleEvent extends Event {

    private String newGroupTitle;

    public UpdateGroupTitleEvent(Long groupId, String userId, String newGroupTitle) {
        super(groupId, userId);
        this.newGroupTitle = newGroupTitle;
    }

    @Override
    public void apply(Group group) {
        group.setTitle(this.newGroupTitle);
    }

}
