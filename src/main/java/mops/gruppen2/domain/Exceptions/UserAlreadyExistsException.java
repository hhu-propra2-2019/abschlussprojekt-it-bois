package mops.gruppen2.domain.Exceptions;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends EventException {
    public UserAlreadyExistsException(String msg) {
        super(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public UserAlreadyExistsException() {
        super("Der User existiert bereits.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
