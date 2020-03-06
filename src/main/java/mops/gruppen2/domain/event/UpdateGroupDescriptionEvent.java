package mops.gruppen2.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class UpdateGroupDescriptionEvent extends Event {
    String newGroupDescription;

    public UpdateGroupDescriptionEvent(long event_id, long group_id, String user_id, String newGroupDescription) {
        super(event_id, group_id, user_id);
        this.newGroupDescription = newGroupDescription;
    }
}
