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
@NoArgsConstructor
public class UpdateGroupTitleEvent extends Event {
    String newGroupTitle;

    public UpdateGroupTitleEvent(Long group_id, String user_id, String newGroupTitle) {
        super(group_id, user_id);
        this.newGroupTitle = newGroupTitle;
    }

    @Override
    public void applyEvent(Group group) {
        group.setTitle(this.newGroupTitle);
    }

}
