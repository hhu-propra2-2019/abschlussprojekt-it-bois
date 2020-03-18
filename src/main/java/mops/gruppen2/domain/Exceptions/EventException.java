package mops.gruppen2.domain.Exceptions;

public class EventException extends Exception {

    private String msg;

    public EventException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
