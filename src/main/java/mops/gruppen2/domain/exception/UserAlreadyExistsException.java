package mops.gruppen2.domain.exception;

public class UserAlreadyExistsException extends EventException {

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
