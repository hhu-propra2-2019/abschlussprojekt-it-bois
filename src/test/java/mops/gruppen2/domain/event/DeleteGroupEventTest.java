package mops.gruppen2.domain.event;

import mops.gruppen2.TestBuilder;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Visibility;
import org.junit.jupiter.api.Test;

import static mops.gruppen2.TestBuilder.uuidMock;
import static org.assertj.core.api.Assertions.assertThat;

class DeleteGroupEventTest {

    @Test
    void applyEvent() {
        Event createEvent = new CreateGroupEvent(uuidMock(0),
                                                 "A",
                                                 uuidMock(1),
                                                 GroupType.SIMPLE,
                                                 Visibility.PUBLIC,
                                                 100L);
        Event deleteEvent = new DeleteGroupEvent(uuidMock(0), "A");

        Group group = TestBuilder.apply(createEvent, deleteEvent);

        assertThat(group.getMembers()).isEmpty();
        assertThat(group.getType()).isEqualTo(null);
        assertThat(group.getVisibility()).isEqualTo(null);
        assertThat(group.getUserMaximum()).isEqualTo(0);
        assertThat(group.getId()).isEqualTo(uuidMock(0));
        assertThat(group.getParent()).isEqualTo(null);
    }
}
