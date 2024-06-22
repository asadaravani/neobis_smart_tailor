package kg.neobis.smarttailor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {

    ConfirmationCodeRepository confirmationCodeRepository;
    SpringTemplateEngine engine;
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
    public void sendEmail(MimeMessage email) {

        javaMailSender.send(email);
    }

    @Override
    public MimeMessage createMail(AppUser user, ConfirmationCode confirmationCode) throws MessagingException {



        Context context = new Context();
        context.setVariable("confirmCode", confirmationCode.getCode());
        String emailBody = engine.process("confirmation_code", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(emailBody, true);
        helper.setTo(user.getEmail());
        helper.setSubject("Код верификации.");
        helper.setFrom("smart_tailor@gmail.com");
        return mimeMessage;


    }
}