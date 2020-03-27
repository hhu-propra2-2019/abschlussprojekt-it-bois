package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class NoInviteExistException extends EventException {

    public NoInviteExistException(String info) {
        super(HttpStatus.NOT_FOUND, "Für diese Gruppe existiert kein Link.", info);
    }
}
