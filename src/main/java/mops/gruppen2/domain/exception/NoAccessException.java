package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class NoAccessException extends EventException {
    public NoAccessException(String info) {
        super(HttpStatus.FORBIDDEN, "Hier hast du leider keinen Zugriff!", info);
    }
}
