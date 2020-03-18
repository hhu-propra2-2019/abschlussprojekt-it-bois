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

    public AddUserEvent(Long groupId, String userId, String givenname, String familyname, String email) {
        super(groupId, userId);
        this.givenname = givenname;
        this.familyname = familyname;
        this.email = email;
    }

    @Override
    public void apply(Group group) throws EventException {
        User user = new User(this.userId, this.givenname, this.familyname, this.email);

        if (group.getMembers().contains(user)) {
            throw new UserAlreadyExistsException("Der User existiert bereits");
        }

        group.getMembers().add(user);
        group.getRoles().put(userId, Role.MEMBER);
    }
}
