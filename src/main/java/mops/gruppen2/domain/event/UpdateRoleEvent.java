package mops.gruppen2.domain.event;

import lombok.Getter;
import mops.gruppen2.domain.Role;

@Getter
public class UpdateRoleEvent extends Event {

    private final Role role;

    public UpdateRoleEvent(long id, long gruppe_id, String user_id, Role newRole) {
        super(id, gruppe_id, user_id);

        this.role = newRole;
    }
}
