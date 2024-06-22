package kg.neobis.smarttailor.exception;

public class InvalidRequestException extends BaseException {
    public InvalidRequestException(String message, Integer status) {
        super(message, status);
    }
}