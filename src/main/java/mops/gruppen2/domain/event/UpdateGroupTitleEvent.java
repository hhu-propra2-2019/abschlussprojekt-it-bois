package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Ändert nur den Gruppentitel.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupTitleEvent extends Event {
    String newGroupTitle;

    public UpdateGroupTitleEvent(Long event_id, Long group_id, String user_id, String newGroupTitle) {
        super(event_id, group_id, user_id);
        this.newGroupTitle = newGroupTitle;
    }

    public UpdateGroupTitleEvent(Long group_id, String user_id, String newGroupTitle) {
        super(group_id, user_id);
        this.newGroupTitle = newGroupTitle;
    }
}
