package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class WrongFileException extends EventException {

    public WrongFileException(String info) {
        super(HttpStatus.BAD_REQUEST, "Die entsprechende Datei ist keine valide CSV-Datei!", info);
    }
}
