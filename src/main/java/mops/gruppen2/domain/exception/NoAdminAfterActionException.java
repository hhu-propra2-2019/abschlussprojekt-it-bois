package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class NoAdminAfterActionException extends EventException {

    public NoAdminAfterActionException(String info) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Nach dieser Aktion hätte die Gruppe keinen Admin mehr", info);
    }
}
