package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class InvalidJsonException extends BaseException{
    public InvalidJsonException(String message, HttpStatus status) {
        super(message, status);
    }
}