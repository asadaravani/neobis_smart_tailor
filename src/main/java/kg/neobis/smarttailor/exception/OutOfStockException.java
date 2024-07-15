package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class OutOfStockException extends BaseException{
    public OutOfStockException(String message, HttpStatus status) {
        super(message, status);
    }
}