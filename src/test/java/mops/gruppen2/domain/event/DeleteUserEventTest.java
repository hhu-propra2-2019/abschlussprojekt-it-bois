package mops.gruppen2.domain.event;

import mops.gruppen2.TestBuilder;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import static mops.gruppen2.TestBuilder.addUserEvent;
import static mops.gruppen2.TestBuilder.createPublicGroupEvent;
import static mops.gruppen2.TestBuilder.uuidFromInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteUserEventTest {

    @Test
    void applyEvent() {
        Event createEvent = createPublicGroupEvent(uuidFromInt(0));
        Event addEvent = addUserEvent(uuidFromInt(0), "A");
        Event deleteEvent = new DeleteUserEvent(uuidFromInt(0), "A");

        Group group = TestBuilder.apply(createEvent, addEvent, deleteEvent);

        assertThat(group.getMembers()).hasSize(0);
    }

    @Test
    void applyEvent_userNotFound() {
        Event createEvent = createPublicGroupEvent(uuidFromInt(0));
        Event addEvent = addUserEvent(uuidFromInt(0), "A");
        Event deleteEvent = new DeleteUserEvent(uuidFromInt(0), "B");

        Group group = TestBuilder.apply(createEvent, addEvent);

        assertThrows(UserNotFoundException.class, () -> deleteEvent.apply(group));
        assertThat(group.getMembers()).hasSize(1);
    }
}
