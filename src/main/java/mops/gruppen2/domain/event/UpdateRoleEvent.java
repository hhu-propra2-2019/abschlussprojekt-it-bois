package mops.gruppen2.domain.event;

import lombok.Getter;
import mops.gruppen2.domain.Role;

@Getter
public class UpdateRoleEvent extends Event {

    private final Role newRole;

    public UpdateRoleEvent(long event_id, long group_id, String user_id, Role newRole) {
        super(event_id, group_id, user_id);

        this.newRole = newRole;
    }
}
