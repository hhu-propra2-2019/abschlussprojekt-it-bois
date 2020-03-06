package mops.gruppen2.events;

import lombok.Getter;
import mops.gruppen2.entities.Rolle;

@Getter
public class UpdateRoleEvent extends Event {

    private final Rolle role;

    public UpdateRoleEvent(long id, long gruppe_id, String user_id, Rolle newRole) {
        super(id, gruppe_id, user_id);

        this.role = newRole;
    }
}
