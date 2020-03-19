package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class GroupNotFoundException extends EventException {

    public GroupNotFoundException(String info) {
        super(HttpStatus.NOT_FOUND, "Gruppe wurde nicht gefunden.", info);
    }
}
