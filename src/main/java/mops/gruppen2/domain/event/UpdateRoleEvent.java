package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Exceptions.UserNotFoundException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;

/**
 * Aktualisiert die Gruppenrolle eines Teilnehmers.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleEvent extends Event {

    private Role newRole;

    public UpdateRoleEvent(Long group_id, String user_id, Role newRole) {
        super(group_id, user_id);
        this.newRole = newRole;
    }

    @Override
    public void applyEvent(Group group) throws UserNotFoundException {
        if (group.getRoles().containsKey(user_id)) {
            group.getRoles().put(this.user_id, this.newRole);
        }

        throw new UserNotFoundException(this.getClass().toString());
    }

}
