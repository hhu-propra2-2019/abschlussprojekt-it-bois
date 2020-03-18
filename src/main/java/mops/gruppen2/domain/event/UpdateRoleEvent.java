package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.exception.UserNotFoundException;

/**
 * Aktualisiert die Gruppenrolle eines Teilnehmers.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor // For Jackson
public class UpdateRoleEvent extends Event {

    private Role newRole;

    public UpdateRoleEvent(Long groupId, String userId, Role newRole) {
        super(groupId, userId);
        this.newRole = newRole;
    }
    @Override
    public void applyEvent(Group group) throws UserNotFoundException {
        if (group.getRoles().containsKey(this.userId)) {
            group.getRoles().put(this.userId, this.newRole);
        }

        throw new UserNotFoundException(this.getClass().toString());
    }

}
