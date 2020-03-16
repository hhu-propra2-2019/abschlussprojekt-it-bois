package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;

/**
 * Fügt einen einzelnen Nutzer einer Gruppe hinzu.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserEvent extends Event {
    String givenname;
    String familyname;
    String email;

    public AddUserEvent(Long group_id, String user_id, String givenname, String familyname, String email) {
        super(group_id, user_id);
        this.givenname = givenname;
        this.familyname = familyname;
        this.email = email;
    }

    public void apply(Group group) {
        User user = new User(this.user_id, this.givenname, this.familyname, this.email);
        group.getMembers().add(user);
    }
}
