package mops.gruppen2.domain.event;

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
        User user = new User(userId, givenname, familyname, email);

        if (group.getMembers().contains(user)) {
            throw new UserAlreadyExistsException(getClass().toString());
        }

        if (group.getMembers().size() >= group.getUserMaximum()) {
            throw new GroupFullException(getClass().toString());
        }

        group.getMembers().add(user);
        group.getRoles().put(userId, Role.MEMBER);
    }
}
