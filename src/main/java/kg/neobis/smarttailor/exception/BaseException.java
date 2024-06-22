package kg.neobis.smarttailor.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseException extends RuntimeException {

    Integer status;

    public BaseException(String message, Integer status) {
        super(message);
        this.status = status;
    }
}