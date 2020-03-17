package mops.gruppen2.domain.event;

import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.UserAlreadyExistsException;
import mops.gruppen2.domain.Exceptions.UserNotFoundException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import org.junit.jupiter.api.Test;

import static mops.gruppen2.domain.Role.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DeleteUserEventTest {

    @Test
    void applyDeleteUser() throws EventException {
        Group group = new Group();

        User user = new User("user1","Stein", "Speck", "@sdasd");

        group.getMembers().add(user);
        group.getRoles().put("user1", MEMBER);

        User user2 = new User("user2","Rock", "Roll", "and");

        group.getMembers().add(user2);
        group.getRoles().put("user2", MEMBER);

        Event event = new DeleteUserEvent(1L, "user1");

        event.apply(group);

        assertThat(group.getMembers().size()).isEqualTo(1);
        assertThat(group.getRoles().size()).isEqualTo(1);

    }

    @Test
    void userDoesNotExistExeption() {
        Group group = new Group();

        User user = new User("user1","Stein", "Speck", "@sdasd");

        group.getMembers().add(user);
        group.getRoles().put("user1", MEMBER);

        Event event = new DeleteUserEvent(17L,"user5");

        assertThrows(UserNotFoundException.class, ()->
                event.apply(group)
        );

        assertThat(group.getMembers().size()).isEqualTo(1);
    }
}