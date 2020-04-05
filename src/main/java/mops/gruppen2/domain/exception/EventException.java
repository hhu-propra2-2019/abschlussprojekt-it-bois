package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EventException extends ResponseStatusException {

    public EventException(HttpStatus status, String msg, String info) {
        super(status, msg + "    (" + info + ")");
    }

}
