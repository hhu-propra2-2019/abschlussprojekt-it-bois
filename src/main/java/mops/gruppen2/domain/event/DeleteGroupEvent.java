package mops.gruppen2.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class DeleteGroupEvent extends Event {

    public DeleteGroupEvent(long group_id, String user_id) {
        super(group_id, user_id);
    }

}
