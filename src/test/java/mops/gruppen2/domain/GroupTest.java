package mops.gruppen2.domain;

import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.UpdateRoleEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTest {

    @BeforeEach
    public void setUp(){
    }


    @Disabled
    @Test
    void applyEvent() {
    }

    @Test
    void createSingleGroup() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1,2, "asd", "hello", "foo");

        Group group = new Group();
        group.apply(createGroupEvent);

        assertThat(group.getDescription()).isEqualTo("foo");
        assertThat(group.getTitle()).isEqualTo("hello");
        assertThat(group.getId()).isEqualTo(2);
    }

    // Verwendet CreateGroupEvent
    @Test
    void addSingleUser() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1,1,"prof1", "hi", "foo");
        Group group = new Group();
        group.apply(createGroupEvent);

        User user = new User("prof", "jens", "bendi", "hi@gmail.com");
        AddUserEvent addUserEvent = new AddUserEvent(1,1, user);
        group.apply(addUserEvent);

        assertThat(group.getMembers().get(0)).isEqualTo(user);
    }

    // Verwendet CreateGroupEvent und AddUserEvent
    @Test
    void updateRoleForExistingUser() {
        // Arrange
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, 1L, "1L", "gruppe1", "Eine Testgruppe");
        AddUserEvent addUserEvent = new AddUserEvent(1L, 1L, "5L", "Peter", "Pan", "123@mail.de");

        Group group = new Group();
        group.apply(createGroupEvent);
        group.apply(addUserEvent);

        UpdateRoleEvent updateRoleEvent = new UpdateRoleEvent(1L, 1L, "5L", Role.ORGA);

        // Act
        group.apply(updateRoleEvent);

        // Assert
        assertThat(group.getRoles())
                .containsOnlyKeys(group.getMembers().get(0))
                .containsValue(Role.ORGA);
    }

    @Disabled
    @Test
    void updateRoleForNonExistingUser() {

    }

}
