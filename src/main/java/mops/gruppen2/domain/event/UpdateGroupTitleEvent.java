package mops.gruppen2.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Ändert nur den Gruppentitel.
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class UpdateGroupTitleEvent extends Event {
    String newGroupTitle;

    public UpdateGroupTitleEvent(long event_id, long group_id, String user_id, String newGroupTitle) {
        super(event_id, group_id, user_id);
        this.newGroupTitle = newGroupTitle;
    }
}
