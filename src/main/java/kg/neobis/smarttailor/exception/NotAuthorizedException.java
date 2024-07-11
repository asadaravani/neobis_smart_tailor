package kg.neobis.smarttailor.exception;

public class NotAuthorizedException extends BaseException{
    public NotAuthorizedException(String message, Integer status) {
        super(message, status);
    }
}
