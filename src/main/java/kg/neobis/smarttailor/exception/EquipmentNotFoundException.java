package kg.neobis.smarttailor.exception;

public class EquipmentNotFoundException extends BaseException{
    public EquipmentNotFoundException(String message, Integer status) {
        super(message, status);
    }
}
