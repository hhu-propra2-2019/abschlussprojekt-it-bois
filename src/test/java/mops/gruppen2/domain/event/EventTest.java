package mops.gruppen2.domain.event;

import mops.gruppen2.TestBuilder;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.exception.GroupIdMismatchException;
import org.junit.jupiter.api.Test;

import static mops.gruppen2.TestBuilder.createPublicGroupEvent;
import static mops.gruppen2.TestBuilder.uuidFromInt;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventTest {

    @Test
    void apply() {
        Event createEvent = createPublicGroupEvent(uuidFromInt(0));
        Event addEvent = TestBuilder.addUserEvent(uuidFromInt(1));

        Group group = TestBuilder.apply(createEvent);

        assertThrows(GroupIdMismatchException.class, () -> addEvent.apply(group));
    }

}
