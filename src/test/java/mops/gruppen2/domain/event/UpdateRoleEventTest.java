package mops.gruppen2.domain.event;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import static mops.gruppen2.TestBuilder.addUserEvent;
import static mops.gruppen2.TestBuilder.apply;
import static mops.gruppen2.TestBuilder.createPublicGroupEvent;
import static mops.gruppen2.TestBuilder.uuidFromInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateRoleEventTest {

    @Test
    void applyEvent() {
        Event createEvent = createPublicGroupEvent(uuidFromInt(0));
        Event addEvent = addUserEvent(uuidFromInt(0), "A");
        Event updateEvent = new UpdateRoleEvent(uuidFromInt(0), "A", Role.ADMIN);

        Group group = apply(createEvent, addEvent, updateEvent);

        assertThat(group.getRoles().get("A")).isEqualTo(Role.ADMIN);
    }

    @Test
    void applyEvent_userNotFound() {
        Event createEvent = createPublicGroupEvent(uuidFromInt(0));
        Event addEvent = addUserEvent(uuidFromInt(0), "A");
        Event updateEvent = new UpdateRoleEvent(uuidFromInt(0), "B", Role.ADMIN);

        Group group = apply(createEvent, addEvent);

        assertThrows(UserNotFoundException.class, () -> updateEvent.apply(group));
        assertThat(group.getRoles().get("A")).isEqualTo(Role.MEMBER);
    }
}
