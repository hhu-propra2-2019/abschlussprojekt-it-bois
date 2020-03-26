package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.GroupFullException;
import mops.gruppen2.domain.exception.UserAlreadyExistsException;

import java.util.UUID;

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

    public AddUserEvent(UUID groupId, String userId, String givenname, String familyname, String email) {
        super(groupId, userId);
        this.givenname = givenname;
        this.familyname = familyname;
        this.email = email;
    }

    @Override
    protected void applyEvent(Group group) throws EventException {
        User user = new User(this.userId, this.givenname, this.familyname, this.email);

        if (group.getMembers().contains(user)) {
            throw new UserAlreadyExistsException(this.getClass().toString());
        }

        if (group.getMembers().size() >= group.getUserMaximum()) {
            throw new GroupFullException(this.getClass().toString());
        }

        group.getMembers().add(user);
        group.getRoles().put(userId, Role.MEMBER);
    }
}
