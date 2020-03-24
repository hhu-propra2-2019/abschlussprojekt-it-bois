package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class PageNotFoundException extends EventException {
    public PageNotFoundException(String info) {
        super(HttpStatus.NOT_FOUND, "Die Seite wurde nicht gefunden!", info);
    }
}
