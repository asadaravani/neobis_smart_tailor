package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class NotAuthorizedException extends BaseException {
    public NotAuthorizedException(String message, HttpStatus status) {
        super(message, status);
    }
}