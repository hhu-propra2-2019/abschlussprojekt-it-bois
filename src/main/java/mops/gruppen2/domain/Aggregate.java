package mops.gruppen2.domain;

import com.google.common.base.Throwables;
import lombok.Getter;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.UserAlreadyExistsException;
import mops.gruppen2.domain.event.Event;

import javax.swing.table.TableRowSorter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.String.format;

/**
 * Repräsentiert viele Events als aggregiertes Objekt.
 */
@Getter
public abstract class Aggregate {

    protected long id;

    /**
     * Ruft die spezifische applyEvent-Methode im entsprechenden Aggregat auf.
     *
     * @param event Event, welches aggregiert wird
     */
    public void applyEvent(Event event) throws EventException {
        try {
            Method method = this.getClass().getDeclaredMethod("applyEvent", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            EventException f = (EventException) e.getTargetException();
            throw f;
        }
    }
}
