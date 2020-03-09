package mops.gruppen2.domain;

import lombok.Getter;
import mops.gruppen2.domain.event.Event;

import java.lang.reflect.Method;

/**
 * Repräsentiert viele Events als aggregiertes Objekt.
 */
public abstract class Aggregate {

    @Getter
    protected long id;

    /**
     * Ruft die spezifische applyEvent-Methode im entsprechenden Aggregat auf.
     *
     * @param event Event, welches aggregiert wird
     */
    public void applyEvent(Event event) {
        try {
            Method method = this.getClass().getDeclaredMethod("applyEvent", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
