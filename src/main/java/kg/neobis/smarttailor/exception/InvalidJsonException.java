package kg.neobis.smarttailor.exception;

public class InvalidJsonException extends BaseException{
    public InvalidJsonException(String message, Integer status) {
        super(message, status);
    }
}
