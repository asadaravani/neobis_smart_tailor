package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class NoPermissionException extends BaseException {
    public NoPermissionException(String message, HttpStatus status) {
        super(message, status);
    }
}