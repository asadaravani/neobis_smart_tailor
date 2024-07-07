package kg.neobis.smarttailor.exception;

public class OutOfStockException extends BaseException{
    public OutOfStockException(String message, Integer status) {
        super(message, status);
    }
}
