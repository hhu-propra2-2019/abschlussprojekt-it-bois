package mops.gruppen2.domain;

import lombok.Getter;
import mops.gruppen2.domain.event.Event;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Repräsentiert viele Events als aggregiertes Objekt.
 */
@Getter
public abstract class Aggregate {

    protected long id;

    public void apply(List<Event> events) {
        events.forEach(this::applyEvent);
    }

    public void apply(Event event) {
        applyEvent(event);
    }

    /**
     * Ruft die spezifische applyEvent-Methode im entsprechenden Aggregat auf.
     *
     * @param event Event, welches aggregiert wird
     */
    private void applyEvent(Event event) {
        try {
            Method method = this.getClass().getDeclaredMethod("applyEvent", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
