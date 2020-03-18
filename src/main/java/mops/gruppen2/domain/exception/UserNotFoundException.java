package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends EventException {

    public UserNotFoundException(String info) {
        super(HttpStatus.NOT_FOUND, "Der User wurde nicht gefunden.", info);
    }
}
