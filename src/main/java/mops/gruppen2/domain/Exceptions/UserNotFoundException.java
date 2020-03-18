package mops.gruppen2.domain.Exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends EventException {
    public UserNotFoundException(String msg) {
        super(msg, HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException() {
        super("Der User wurde nicht gefunden.", HttpStatus.NOT_FOUND);
    }
}
