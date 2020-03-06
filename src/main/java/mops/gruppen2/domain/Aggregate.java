package mops.gruppen2.domain;

import lombok.Getter;
import mops.gruppen2.domain.event.Event;

import java.lang.reflect.Method;

public abstract class Aggregate {

    @Getter
    protected final long id;

    protected Aggregate(long id) {
        this.id = id;
    }

    /**
     * Ruft die spezifische applyEvent-Methode im entsprechenden Aggregat auf.
     *
     * @param event Einzelne Änderung an dem Aggregat
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
