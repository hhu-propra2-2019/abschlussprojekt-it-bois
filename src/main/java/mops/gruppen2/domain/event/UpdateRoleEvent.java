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

    public void apply(Group group) throws UserNotFoundException{
        if (!group.getRoles().containsKey(user_id)){
            throw new UserNotFoundException("Der User wurde nicht gefunden");
        }
        group.getRoles().put(this.user_id, this.newRole);
    }

}
