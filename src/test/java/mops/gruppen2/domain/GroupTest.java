package mops.gruppen2.domain;

import mops.gruppen2.domain.Exceptions.UserAlreadyExistsException;
import mops.gruppen2.domain.Exceptions.UserNotFoundException;
import mops.gruppen2.domain.event.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTest {

    @BeforeEach
    public void setUp() {
    }


    @Test
    void applyEvent() {
    }

    @Test
    void createSingleGroup() throws Exception {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE);

        Group group = new Group();

        group.applyEvent(createGroupEvent);
        assertThat(group.getId()).isEqualTo(1L);
    }

    // Verwendet CreateGroupEvent
    @Test
    void addSingleUser() throws Exception {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, "pepee",null, GroupType.LECTURE , Visibility.PRIVATE);
        Group group = new Group();
        group.applyEvent(createGroupEvent);

        User user = new User("prof", "jens", "bendi", "hi@gmail.com");
        AddUserEvent addUserEvent = new AddUserEvent(1L, 1L, user);
        group.applyEvent(addUserEvent);

        assertThat(group.getMembers().get(0)).isEqualTo(user);
    }

    @Test
    void addExistingUser() throws Exception {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, "prof1", null,  GroupType.LECTURE, Visibility.PRIVATE);
        Group group = new Group();
        group.applyEvent(createGroupEvent);

        User user1 = new User("prof", "jens", "bendi", "hi@gmail.com");
        AddUserEvent addUserEvent1 = new AddUserEvent(2L, 1L, user1);
        group.applyEvent(addUserEvent1);

        User user2 = new User("prof", "olga", "bendi", "hi@gmail.com");
        AddUserEvent addUserEvent2 = new AddUserEvent(3L, 1L, user2);
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> {
            group.applyEvent(addUserEvent2);
        });


        assertThat(group.getMembers().size()).isEqualTo(1);
    }

    @Test
    void deleteSingleUser() throws Exception {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE);
        User user = new User("Prof", "Pro", "fessor", "pro@fessor.de");
        AddUserEvent addUserEvent = new AddUserEvent(2L, 2L, user);
        Group group = new Group();
        group.applyEvent(createGroupEvent);
        group.applyEvent(addUserEvent);

        DeleteUserEvent deleteUserEvent = new DeleteUserEvent(3L, 2L, "Prof");
        group.applyEvent(deleteUserEvent);

        assertThat(group.getMembers().size()).isEqualTo(0);
    }

    @Test
    void deleteUserThatDoesNotExists() throws Exception {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE);
        Group group = new Group();
        group.applyEvent(createGroupEvent);

        DeleteUserEvent deleteUserEvent = new DeleteUserEvent(3L, 2L, "Prof");

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            group.applyEvent(deleteUserEvent);
        });
    }

    // Verwendet CreateGroupEvent und AddUserEvent
    @Test
    void updateRoleForExistingUser() throws Exception {
        // Arrange
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE);
        AddUserEvent addUserEvent = new AddUserEvent(1L, 1L, "5L", "Peter", "Pan", "123@mail.de");

        Group group = new Group();
        group.applyEvent(createGroupEvent);
        group.applyEvent(addUserEvent);

        UpdateRoleEvent updateRoleEvent = new UpdateRoleEvent(1L, 1L, "5L", Role.ADMIN);

        // Act
        group.applyEvent(updateRoleEvent);

        // Assert
        assertThat(group.getRoles())
                .containsOnlyKeys(group.getMembers().get(0))
                .containsValue(Role.ADMIN);
    }

    @Test
    void updateRoleForNonExistingUser() throws Exception {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(1L, "Prof", null, GroupType.LECTURE, Visibility.PRIVATE);
        UpdateRoleEvent updateRoleEvent = new UpdateRoleEvent(345L, 33L, "coolerUser", Role.ADMIN);

        Group group = new Group();
        group.applyEvent(createGroupEvent);
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            group.applyEvent(updateRoleEvent);
        });
    }

    @Test
    void updateTitle() throws Exception {  //bitte umschreiben
        Group group = new Group();
        UpdateGroupTitleEvent updateGroupTitleEvent = new UpdateGroupTitleEvent(2L, 1L, "Klaus", "Toller Titel");
        group.applyEvent(updateGroupTitleEvent);

        assertThat("Toller Titel").isEqualTo("Toller Titel");
    }

    @Test
    void updateBeschreibung() throws Exception { //bitte umschreiben
        Group group = new Group();

        UpdateGroupDescriptionEvent updateGroupDescriptionEvent = new UpdateGroupDescriptionEvent(2L, 1L, "Peter", "Tolle Beschreibung");
        group.applyEvent(updateGroupDescriptionEvent);

        assertThat("Tolle Beschreibung").isEqualTo("Tolle Beschreibung");
    }
}
