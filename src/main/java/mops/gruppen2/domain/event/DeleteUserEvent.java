package mops.gruppen2.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.UserNotFoundException;

/**
 * Entfernt ein einzelnes Mitglied einer Gruppe.
 */
@Getter
@NoArgsConstructor // For Jackson
public class DeleteUserEvent extends Event {

    public DeleteUserEvent(Long groupId, String userId) {
        super(groupId, userId);
    }

    @Override
    public void applyEvent(Group group) throws EventException {
        for (User user : group.getMembers()) {
            if (user.getId().equals(this.userId)) {
                group.getMembers().remove(user);
                group.getRoles().remove(user.getId());
                return;
            }
        }
        throw new UserNotFoundException(this.getClass().toString());
    }
}
