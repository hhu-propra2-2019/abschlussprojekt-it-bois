package mops.gruppen2.domain.event;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import org.junit.jupiter.api.Test;

import static mops.gruppen2.domain.Role.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DeleteUserEventTest {

    @Test
    void apply() {
        Group group = new Group();

        User user = new User("user1","Stein", "Speck", "@sdasd");

        group.getMembers().add(user);
        group.getRoles().put("user1", MEMBER);

        User user2 = new User("user2","Rock", "Roll", "and");

        group.getMembers().add(user2);
        group.getRoles().put("user2", MEMBER);

        Event event = new DeleteUserEvent(1L, "user1");

        event.apply(group);

        assertThat(group.getRoles().size()).isEqualTo(1);
    }
}