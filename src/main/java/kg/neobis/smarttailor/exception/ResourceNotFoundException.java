package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}