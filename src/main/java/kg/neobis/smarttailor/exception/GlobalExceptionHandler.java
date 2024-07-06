package kg.neobis.smarttailor.exception;

import jakarta.validation.ConstraintViolationException;
import kg.neobis.smarttailor.dtos.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleProductException(InvalidRequestException ex) {
        log.error("InvalidRequestException: ({})", ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleProductException(ResourceAlreadyExistsException ex) {
        log.error("ResourceAlreadyExistsException: ({})", ex.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleProductException(ResourceNotFoundException ex) {
        log.error("ResourceNotFoundException: ({})", ex.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        log.error("Internal Server Error: {}", ex.getMessage());
        return new ResponseEntity<>(new BaseException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceProcessingErrorException.class)
    public ResponseEntity<Object> handleResourceProcessing(ResourceProcessingErrorException ex) {
        log.error("ResourceProcessingErrorException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
