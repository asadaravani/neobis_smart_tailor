package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {

    void sendEmail(SimpleMailMessage email);

    SimpleMailMessage createMail(AppUser user, ConfirmationCode confirmationCode);

    ResponseEntity<?> confirmEmail(Integer code);
}