package mops.gruppen2.domain;

import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteUserEvent;
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
        group.applyEvent(createGroupEvent);

        assertThat(group.getDescription()).isEqualTo("foo");
        assertThat(group.getTitle()).isEqualTo("hello");
        assertThat(group.getId()).isEqualTo(2);
    }

    // Verwendet CreateGroupEvent
    @Test
    void addSingleUser() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1,1,"prof1", "hi", "foo");
        Group group = new Group();
        group.applyEvent(createGroupEvent);

        User user = new User("prof", "jens", "bendi", "hi@gmail.com");
        AddUserEvent addUserEvent = new AddUserEvent(1,1, user);
        group.applyEvent(addUserEvent);

        assertThat(group.getMembers().get(0)).isEqualTo(user);
    }

    @Test
    void addExistingUser() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1,1,"prof1", "hi", "foo");
        Group group = new Group();
        group.applyEvent(createGroupEvent);

        User user1 = new User("prof", "jens", "bendi", "hi@gmail.com");
        AddUserEvent addUserEvent1 = new AddUserEvent(2,1, user1);
        group.applyEvent(addUserEvent1);

        User user2 = new User("prof", "olga", "bendi", "hi@gmail.com");
        AddUserEvent addUserEvent2 = new AddUserEvent(3,1, user2);
        group.applyEvent(addUserEvent2);

        assertThat(group.getMembers().size()).isEqualTo(1);
    }

    @Test
    void deleteSingleUser() {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1, 2, "Prof", "Tolle Gruppe", "Tolle Beshreibung");
        User user = new User("Prof", "Pro", "fessor", "pro@fessor.de");
        AddUserEvent addUserEvent = new AddUserEvent(2, 2, user);
        Group group = new Group();
        group.applyEvent(createGroupEvent);
        group.applyEvent(addUserEvent);

        DeleteUserEvent deleteUserEvent = new DeleteUserEvent(3, 2, "Prof");
        group.applyEvent(deleteUserEvent);

        assertThat(group.getMembers().size()).isEqualTo(0);
    }

    // Verwendet CreateGroupEvent und AddUserEvent
    @Test
    void updateRoleForExistingUser() {
        // Arrange
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, 1L, "1L", "gruppe1", "Eine Testgruppe");
        AddUserEvent addUserEvent = new AddUserEvent(1L, 1L, "5L", "Peter", "Pan", "123@mail.de");

        Group group = new Group();
        group.applyEvent(createGroupEvent);
        group.applyEvent(addUserEvent);

        UpdateRoleEvent updateRoleEvent = new UpdateRoleEvent(1L, 1L, "5L", Role.ORGA);

        // Act
        group.applyEvent(updateRoleEvent);

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
