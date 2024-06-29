package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import kg.neobis.smarttailor.dtos.LoginAdmin;
import kg.neobis.smarttailor.dtos.LoginResponse;
import kg.neobis.smarttailor.dtos.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<?> login(String email);

    LoginResponse loginAdmin(LoginAdmin request);

    ResponseEntity<?> logOut(HttpServletRequest request);

    ResponseEntity<?> confirmEmail(String email, Integer code);

    void resendConfirmationCode(String email) throws MessagingException;

    ResponseEntity<?> signUp(SignUpRequest request) throws MessagingException;
}