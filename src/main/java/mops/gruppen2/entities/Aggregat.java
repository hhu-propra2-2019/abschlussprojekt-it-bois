package mops.gruppen2.entities;

import mops.gruppen2.events.Event;

import java.lang.reflect.Method;

public abstract class Aggregat {

    /**
     * Ruft die spezifische applyEvent-Methode im entsprechenden Aggregat auf.
     *
     * @param event
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
