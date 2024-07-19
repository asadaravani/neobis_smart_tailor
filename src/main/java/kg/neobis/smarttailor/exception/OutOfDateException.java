package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class OutOfDateException extends BaseException{
    public OutOfDateException(String message, HttpStatus status) {
        super(message, status);
    }
}