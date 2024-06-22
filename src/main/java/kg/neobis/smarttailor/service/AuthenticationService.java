package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.dtos.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<?> confirmCode(String email, Integer code);

    void resendConfirmationCode(String email) throws MessagingException;

    ResponseEntity<?> signUp(SignUpRequest request) throws MessagingException;
}