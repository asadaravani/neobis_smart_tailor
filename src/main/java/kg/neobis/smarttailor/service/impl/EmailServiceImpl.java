package kg.neobis.smarttailor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {

    SpringTemplateEngine engine;
    JavaMailSender javaMailSender;

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
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(emailBody, true);
        helper.setTo(user.getEmail());
        helper.setSubject("КОД ВЕРИФИКАЦИИ");
        helper.setFrom("smart_tailor@gmail.com");
        return mimeMessage;
    }
}