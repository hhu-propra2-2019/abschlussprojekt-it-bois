package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class InvalidInviteException extends EventException {

    public InvalidInviteException(String info) {
        super(HttpStatus.NOT_FOUND, "Der Einladungslink ist ungültig.", info);
    }
}
