package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.dtos.AddAdminRequest;
import kg.neobis.smarttailor.dtos.LogInRequest;
import kg.neobis.smarttailor.dtos.LogInResponse;
import kg.neobis.smarttailor.dtos.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<?> addAdmin(AddAdminRequest request);

    ResponseEntity<?> logIn(String email);

    LogInResponse logInAdmin(LogInRequest request);

    ResponseEntity<?> confirmEmail(String email, Integer code);

    void resendConfirmationCode(String email) throws MessagingException;

    ResponseEntity<?> signUp(SignUpRequest request) throws MessagingException;
}