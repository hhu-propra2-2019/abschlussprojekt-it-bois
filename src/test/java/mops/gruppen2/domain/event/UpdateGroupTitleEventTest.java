package mops.gruppen2.domain.event;

import mops.gruppen2.TestBuilder;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.exception.BadParameterException;
import org.junit.jupiter.api.Test;

import static mops.gruppen2.TestBuilder.createPublicGroupEvent;
import static mops.gruppen2.TestBuilder.uuidMock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateGroupTitleEventTest {

    @Test
    void applyEvent() {
        Event createEvent = createPublicGroupEvent(uuidMock(0));
        Event updateEvent = new UpdateGroupTitleEvent(uuidMock(0), "A", "title.");

        Group group = TestBuilder.apply(createEvent, updateEvent);

        assertThat(group.getTitle()).isEqualTo("title.");
    }

    @Test
    void applyEvent_badDescription() {
        Event createEvent = createPublicGroupEvent(uuidMock(0));
        Event updateEventA = new UpdateGroupTitleEvent(uuidMock(0), "A", "");
        Event updateEventB = new UpdateGroupTitleEvent(uuidMock(0), "A", "  ");

        Group group = TestBuilder.apply(createEvent);

        assertThrows(BadParameterException.class, () -> updateEventA.apply(group));
        assertThrows(BadParameterException.class, () -> updateEventB.apply(group));
    }
}
