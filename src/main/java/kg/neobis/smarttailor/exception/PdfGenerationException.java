package kg.neobis.smarttailor.exception;

import org.springframework.http.HttpStatus;

public class PdfGenerationException extends BaseException {
    public PdfGenerationException(String message, HttpStatus status) {
        super(message, status);
    }
}