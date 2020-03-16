package mops.gruppen2.domain.event;

import lombok.*;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Group;

/**
 * Entfernt ein einzelnes Mitglied einer Gruppe.
 */
@Getter
public class DeleteUserEvent extends Event {
    public DeleteUserEvent(Long group_id, String user_id) {
        super(group_id, user_id);
    }

    public DeleteUserEvent() {
    }

    public void apply(Group group) {
        for (User user : group.getMembers()) {
            if (user.getUser_id().equals(this.user_id)) {
                group.members.remove(user);
                group.getRoles().remove(user);
                return;
            }
        }
    }
}
