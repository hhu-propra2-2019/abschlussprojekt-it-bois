package mops.gruppen2.domain.Exceptions;

import org.springframework.http.HttpStatus;

public class GroupIdMismatchException extends EventException {
    public GroupIdMismatchException(String msg) {
        super("Falsche Gruppe für Event." + "    (" + msg + ")", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public GroupIdMismatchException() {
        super("", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
