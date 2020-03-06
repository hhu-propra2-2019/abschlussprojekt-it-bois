package mops.gruppen2.entities;

import mops.gruppen2.events.AddUser;
import mops.gruppen2.events.CreateGroupEvent;
import mops.gruppen2.events.UpdateRoleEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GruppeTest {

    AddUser addUser;
    CreateGroupEvent createGroupEvent;

    @BeforeEach
    public void setUp(){

    }


    @Test
    void applyEvent() {
    }

    @Test
    void applyAddUserEvent(){
        Gruppe gruppe = new Gruppe();
        Gruppe testGruppe = new Gruppe();
        Teilnehmer teilnehmer = new Teilnehmer();
        addUser = new AddUser(1L,1L,"prof","jens","bendi", "hi@gmail.com");
        createGroupEvent = new CreateGroupEvent(1L,1L,"prof1", "hi", "foo");

        gruppe.applyEvent(createGroupEvent);
        gruppe.applyEvent(addUser);
        testGruppe.applyEvent(createGroupEvent);
        teilnehmer.setId("prof");
        teilnehmer.setVorname("jens");
        teilnehmer.setNachname("bendi");
        teilnehmer.setEmail("hi@gmail.com");
        List<Teilnehmer> testTeil= new ArrayList<>();
        testTeil.add(teilnehmer);
        testGruppe.setTeilnehmersList(testTeil);

        assertEquals(testGruppe,gruppe);
    }

    // Verwendet CreateGroupEvent und AddUserEvent
    @Test
    void updateRoleForExistingUser() {
        // Arrange
        Gruppe gruppe = new Gruppe();
        Orga orga = new Orga();

        gruppe.applyEvent(new CreateGroupEvent(1L, 1L, "1L", "gruppe1", "Eine Testgruppe"));
        gruppe.applyEvent(new AddUser(1L, 1L, "5L", "Peter", "Pan", "123@mail.de"));

        // Act
        gruppe.applyEvent(new UpdateRoleEvent(1L, 1L, "5L", orga));

        // Assert
        assertThat(gruppe.getRollenList())
                .containsOnlyKeys(gruppe.getTeilnehmersList().get(0))
                .containsValue(orga);
    }

    @Test
    void applyCreteGroupEvent() {
        String userId = "asd";
        CreateGroupEvent event = new CreateGroupEvent(1L,2,userId, "hello", "foo");

        Gruppe gruppe1 = new Gruppe();
        gruppe1.applyEvent(event);

        Gruppe gruppe2 = new Gruppe();
        gruppe2.id = 2L;
        gruppe2.titel = "hello";
        gruppe2.beschreibung = "foo";
        gruppe2.teilnehmersList = new ArrayList<>();
        gruppe2.rollenList = new HashMap<>();

        assertEquals(gruppe2, gruppe1);
    }

}