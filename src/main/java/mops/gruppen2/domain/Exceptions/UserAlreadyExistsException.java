package mops.gruppen2.domain.Exceptions;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends EventException {

    public UserAlreadyExistsException(String info) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Der User existiert bereits.", info);
    }
}
