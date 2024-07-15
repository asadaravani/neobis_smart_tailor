package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {
    public InvalidRequestException(String message, HttpStatus status) {
        super(message, status);
    }
}