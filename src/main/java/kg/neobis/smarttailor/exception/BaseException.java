package kg.neobis.smarttailor.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }
}