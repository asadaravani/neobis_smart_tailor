package kg.neobis.smarttailor.exception;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException(){
        super("User Not Found");
    }
}
