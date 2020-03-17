package mops.gruppen2.domain.event;

import lombok.*;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.UserNotFoundException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Group;

/**
 * Entfernt ein einzelnes Mitglied einer Gruppe.
 */
@Getter
@NoArgsConstructor
public class DeleteUserEvent extends Event {
    public DeleteUserEvent(Long group_id, String user_id) {
        super(group_id, user_id);
    }

    public void apply(Group group) throws EventException {
        for (User user : group.getMembers()) {
            if (user.getUser_id().equals(this.user_id)) {
                group.getMembers().remove(user);
                group.getRoles().remove(user.getUser_id());
                return;
            }
        }
        throw new UserNotFoundException("Der User existiert nicht");
    }
}
