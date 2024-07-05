package kg.neobis.smarttailor.exception;

public class ResourceProcessingErrorException extends BaseException{
    public ResourceProcessingErrorException(String message, Integer status) {
        super(message, status);
    }
}
