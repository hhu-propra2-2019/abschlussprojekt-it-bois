package mops.gruppen2.domain.event;

import lombok.*;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;

/**
 * Entfernt ein einzelnes Mitglied einer Gruppe.
 */
@Getter
public class DeleteUserEvent extends Event { 
    public DeleteUserEvent(Long group_id, String user_id) {
        super(group_id, user_id);
    }

    public DeleteUserEvent() {}

    public void apply(Group group, User user) {
        group.getMembers().remove(user);
    }
}
