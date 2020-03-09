package mops.gruppen2.domain.event;

import lombok.*;
import mops.gruppen2.domain.Role;

/**
 * Aktualisiert die Gruppenrolle eines Teilnehmers.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleEvent extends Event {

    Role newRole;

    public UpdateRoleEvent(long event_id, long group_id, String user_id, Role newRole) {
        super(event_id, group_id, user_id);

        this.newRole = newRole;
    }
}
