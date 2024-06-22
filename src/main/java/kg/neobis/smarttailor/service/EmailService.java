package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {

    void sendEmail(MimeMessage email);

    MimeMessage createMail(AppUser user, ConfirmationCode confirmationCode) throws MessagingException;

    ResponseEntity<?> confirmEmail(Integer code);
}