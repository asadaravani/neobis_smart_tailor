package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<?> confirmCode(String email, Integer code);

    void resendConfirmationCode(String email);

    ResponseEntity<?> signUp(SignUpRequest request);
}