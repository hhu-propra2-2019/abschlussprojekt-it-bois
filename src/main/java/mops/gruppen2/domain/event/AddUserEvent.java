package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.UserAlreadyExistsException;

/**
 * Fügt einen einzelnen Nutzer einer Gruppe hinzu.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor // For Jackson
public class AddUserEvent extends Event {

    private String givenname;
    private String familyname;
    private String email;

    public AddUserEvent(Long group_id, String user_id, String givenname, String familyname, String email) {
        super(group_id, user_id);
        this.givenname = givenname;
        this.familyname = familyname;
        this.email = email;
    }

    public void apply(Group group) throws EventException {
        User user = new User(this.user_id, this.givenname, this.familyname, this.email);

        if (group.getMembers().contains(user)) {
            throw new UserAlreadyExistsException("Der User existiert bereits");
        }

        group.getMembers().add(user);
        group.getRoles().put(user_id, Role.MEMBER);
    }
}
