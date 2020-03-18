package mops.gruppen2.domain.Exceptions;

public class UserAlreadyExistsException extends EventException {

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
