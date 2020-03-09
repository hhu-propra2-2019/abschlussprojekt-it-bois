package mops.gruppen2.domain.Exceptions;

public class UserNotFoundException extends EventException{
    public  UserNotFoundException(String msg){
        super(msg);
    }
}
