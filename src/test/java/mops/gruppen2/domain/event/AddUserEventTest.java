package mops.gruppen2.domain.event;

import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.UserAlreadyExistsException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import org.junit.jupiter.api.Test;

import static mops.gruppen2.domain.Role.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AddUserEventTest {

    @Test
    public void userAllreadyExistExeption() throws EventException {
        Group group = new Group();

        User user = new User("user1","Stein", "Speck", "@sdasd");

        group.getMembers().add(user);

        Event event1 = new AddUserEvent(4L, "user2", "Rock", "Roll", "and");
        event1.apply(group);

        Event event2 = new AddUserEvent(4L, "user1", "Rock", "Roll", "and");

        assertThrows(UserAlreadyExistsException.class, ()->
                event2.apply(group)
                );
        assertThat(group.getMembers().size()).isEqualTo(2);
    }


}