package mops.gruppen2.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class DeleteUserEvent extends Event{

    public DeleteUserEvent(long event_id, long group_id, String user_id) {
        super(event_id, group_id, user_id);
    }
}
