package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class NoValueException extends EventException {

    public NoValueException(String info) {
        super(HttpStatus.BAD_REQUEST, "Eine Information fehlt.", info);
    }
}
