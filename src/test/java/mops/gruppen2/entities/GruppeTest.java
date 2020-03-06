package mops.gruppen2.entities;

import mops.gruppen2.events.AddUser;
import mops.gruppen2.events.CreateGroupEvent;
import mops.gruppen2.events.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
        addUser = new AddUser(1L,1L,1L,"jens","bendi", "hi@gmail.com");
        createGroupEvent = new CreateGroupEvent(1L,1L,1L, "hi", "foo");

        gruppe.applyEvent(createGroupEvent);
        gruppe.applyEvent(addUser);
        testGruppe.applyEvent(createGroupEvent);
        teilnehmer.setId(1L);
        teilnehmer.setVorname("jens");
        teilnehmer.setNachname("bendi");
        teilnehmer.setEmail("hi@gmail.com");
        List<Teilnehmer> testTeil= new ArrayList<>();
        testTeil.add(teilnehmer);
        testGruppe.setTeilnehmersList(testTeil);

        assertEquals(testGruppe,gruppe);
    }
}