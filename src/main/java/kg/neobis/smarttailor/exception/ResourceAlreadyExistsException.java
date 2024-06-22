package kg.neobis.smarttailor.exception;

public class ResourceAlreadyExistsException extends BaseException{
    public ResourceAlreadyExistsException(String message, Integer status) {
        super(message, status);
    }
}