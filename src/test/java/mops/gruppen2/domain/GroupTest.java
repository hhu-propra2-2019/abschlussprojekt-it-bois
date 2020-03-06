package mops.gruppen2.domain;

import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.UpdateRoleEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupTest {

    AddUserEvent addUserEvent;
    CreateGroupEvent createGroupEvent;

    @BeforeEach
    public void setUp(){
    }


    @Test
    void applyEvent() {
    }

    @Test
    void applyCreateGroupEvent() {
        String userId = "asd";
        CreateGroupEvent event = new CreateGroupEvent(1L,2,userId, "hello", "foo");

        Group group1 = new Group();
        group1.applyEvent(event);

        Group group2 = new Group();
        group2.id = 2L;
        group2.title = "hello";
        group2.description = "foo";
        group2.members = new ArrayList<>();
        group2.roles = new HashMap<>();

        assertEquals(group2, group1);
    }

    @Test
    void applyAddUserEvent(){
        Group group = new Group();
        // Group testGroup = new Group();
        User user = new User("prof", "jens", "bendi", "hi@gmail.com");
        createGroupEvent = new CreateGroupEvent(1L,1L,"prof1", "hi", "foo");
        addUserEvent = new AddUserEvent(1L,1L, user);

        group.applyEvent(createGroupEvent);
        group.applyEvent(addUserEvent);
        // testGroup.applyEvent(createGroupEvent);
        // List<User> testTeil = new ArrayList<>();
        // testTeil.add(user);
        // testGroup.setMembers(testTeil);

        assertThat(group.getMembers().get(0)).isEqualTo(user);
    }

    // Verwendet CreateGroupEvent und AddUserEvent
    @Test
    void updateRoleForExistingUser() {
        // Arrange
        Group group = new Group();

        group.applyEvent(new CreateGroupEvent(1L, 1L, "1L", "gruppe1", "Eine Testgruppe"));
        group.applyEvent(new AddUserEvent(1L, 1L, "5L", "Peter", "Pan", "123@mail.de"));

        // Act
        group.applyEvent(new UpdateRoleEvent(1L, 1L, "5L", Role.ORGA));

        // Assert
        assertThat(group.getRoles())
                .containsOnlyKeys(group.getMembers().get(0))
                .containsValue(Role.ORGA);
    }

}
