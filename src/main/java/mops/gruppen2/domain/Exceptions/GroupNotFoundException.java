package mops.gruppen2.domain.Exceptions;

import org.springframework.http.HttpStatus;

public class GroupNotFoundException extends EventException {
    public GroupNotFoundException(String msg) {
        super(msg, HttpStatus.NOT_FOUND);
    }

    public GroupNotFoundException() {
        super("Gruppe nicht gefunden.", HttpStatus.NOT_FOUND);
    }
}
