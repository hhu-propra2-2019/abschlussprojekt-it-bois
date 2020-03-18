package mops.gruppen2.domain.Exceptions;

import org.springframework.http.HttpStatus;

public class NoValueException extends EventException {

    public NoValueException(String info) {
        super(HttpStatus.NO_CONTENT, "Eine Information fehlt.", info);
    }
}
