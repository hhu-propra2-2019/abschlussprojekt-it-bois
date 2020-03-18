package mops.gruppen2.domain.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EventException extends ResponseStatusException {
    private String msg;

    public EventException(String msg, HttpStatus status) {
        super(status, msg);
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
