package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class ResourceProcessingErrorException extends BaseException {
    public ResourceProcessingErrorException(String message, HttpStatus status) {
        super(message, status);
    }
}