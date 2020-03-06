package mops.gruppen2.entities;

import mops.gruppen2.events.CreateGroupEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GruppeTest {

    @Test
    void applyCreteGroupEvent() {
        CreateGroupEvent event = new CreateGroupEvent(1L,2L,3L,"hello", "foo");

        Gruppe gruppe1 = new Gruppe();
        gruppe1.applyEvent(event);

        Gruppe gruppe2 = new Gruppe();
        gruppe2.id = 2;
        gruppe2.titel = "hello";
        gruppe2.beschreibung = "foo";

        assertEquals(gruppe2, gruppe1);
    }
}