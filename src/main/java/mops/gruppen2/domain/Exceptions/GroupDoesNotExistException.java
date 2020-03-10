package mops.gruppen2.domain.Exceptions;

import mops.gruppen2.domain.event.Event;

public class GroupDoesNotExistException extends EventException {
    public GroupDoesNotExistException(String msg) {
        super(msg);
    }
}
