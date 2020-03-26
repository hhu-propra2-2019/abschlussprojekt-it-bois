package mops.gruppen2.domain.event;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddUserEventTest {

    @Test
    void userAlreadyExistExeption() throws EventException {
        Group group = new Group();
        User user = new User("user1", "Stein", "Speck", "@sdasd");
        group.getMembers().add(user);
        group.setUserMaximum(10L);
        UUID id = UUID.randomUUID();
        Event event1 = new AddUserEvent(id, "user2", "Rock", "Roll", "and");
        event1.apply(group);

        Event event2 = new AddUserEvent(id, "user1", "Rock", "Roll", "and");

        assertThrows(UserAlreadyExistsException.class, () ->
                event2.apply(group)
        );
        assertThat(group.getMembers().size()).isEqualTo(2);
    }


}
