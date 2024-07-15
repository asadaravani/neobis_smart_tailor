package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException{
    public ResourceAlreadyExistsException(String message, HttpStatus status) {
        super(message, status);
    }
}