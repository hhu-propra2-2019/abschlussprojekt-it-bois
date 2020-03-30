package mops.gruppen2.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.exception.UserNotFoundException;

import java.util.UUID;

/**
 * Aktualisiert die Gruppenrolle eines Teilnehmers.
 */
@Getter
@NoArgsConstructor // For Jackson
public class UpdateRoleEvent extends Event {

    private Role newRole;

    public UpdateRoleEvent(UUID groupId, String userId, Role newRole) {
        super(groupId, userId);
        this.newRole = newRole;
    }

    @Override
    protected void applyEvent(Group group) throws UserNotFoundException {
        if (group.getRoles().containsKey(userId)) {
            group.getRoles().put(userId, newRole);
            return;
        }

        throw new UserNotFoundException(getClass().toString());
    }

}
