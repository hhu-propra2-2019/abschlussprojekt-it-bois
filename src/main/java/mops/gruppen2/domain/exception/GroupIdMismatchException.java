package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class GroupIdMismatchException extends EventException {

    public GroupIdMismatchException(String info) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Falsche Gruppe für Event.", info);
    }
}
