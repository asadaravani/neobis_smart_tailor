package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.repository.ConfirmationCodeRepository;
import kg.neobis.smarttailor.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {

    ConfirmationCodeRepository confirmationCodeRepository;
    JavaMailSender javaMailSender;

    @Override
    public ResponseEntity<?> confirmEmail(Integer code) {

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByCode(code);

        if (confirmationCode != null && !confirmationCode.isExpired()) {
            confirmationCode.setConfirmedAt(LocalDateTime.now());
            confirmationCodeRepository.save(confirmationCode);
            return ResponseEntity.ok("Email verified successfully!");
        }
        return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }

    @Override
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Override
    public SimpleMailMessage createMail(AppUser user, ConfirmationCode confirmationCode) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Email Verification");
        mailMessage.setText("Confirmation code: " + confirmationCode.getCode());

        return mailMessage;
    }
}