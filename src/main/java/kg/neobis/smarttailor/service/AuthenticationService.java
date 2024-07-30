package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import kg.neobis.smarttailor.dtos.AccessToken;
import kg.neobis.smarttailor.dtos.LoginAdmin;
import kg.neobis.smarttailor.dtos.LoginResponse;
import kg.neobis.smarttailor.dtos.SignUpRequest;

public interface AuthenticationService {

    LoginResponse confirmEmail(String email, Integer code);

    String login(String email);

    LoginResponse loginAdmin(LoginAdmin request);

    String logOut(HttpServletRequest request);

    AccessToken refreshToken(String refreshToken);

    String resendConfirmationCode(String email) throws MessagingException;

    String signUp(SignUpRequest request) throws MessagingException;
}