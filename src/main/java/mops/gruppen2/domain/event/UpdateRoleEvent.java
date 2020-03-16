package mops.gruppen2.domain.event;

import lombok.*;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.UserNotFoundException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;

import java.util.Optional;

/**
 * Aktualisiert die Gruppenrolle eines Teilnehmers.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleEvent extends Event {

    Role newRole;
  
    public UpdateRoleEvent(Long group_id, String user_id, Role newRole) {
        super(group_id, user_id);
        this.newRole = newRole;
    }

    public void apply(Group group) {
        User user = new User(user_id, null, null, null);

        Optional<User> userOptional = group.getMembers().stream()
                .filter(u -> u.getUser_id().equals(user_id))
                .findFirst();

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
        }

        if (group.getRoles().containsKey(user) && newRole == Role.MEMBER) {
            group.getRoles().remove(user);
        } else {
            group.getRoles().put(user.getUser_id(), newRole);
        }
    }

}
