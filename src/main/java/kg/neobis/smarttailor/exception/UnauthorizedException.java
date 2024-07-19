package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message, HttpStatus status) {
        super(message, status);
    }
}