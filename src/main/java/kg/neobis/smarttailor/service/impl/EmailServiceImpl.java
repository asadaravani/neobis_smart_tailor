package kg.neobis.smarttailor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.entity.InvitationToken;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.Position;
import kg.neobis.smarttailor.entity.SubscriptionToken;
import kg.neobis.smarttailor.service.ConfirmationCodeService;
import kg.neobis.smarttailor.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {

    ConfirmationCodeService confirmationCodeService;
    SpringTemplateEngine engine;
    JavaMailSender javaMailSender;

    @Override
    public MimeMessage createInvitationEmployeeMail(AppUser user, String employeeEmail, InvitationToken token, Organization organization, Position position) throws MessagingException {

        Context context = new Context();
        context.setVariable("invitationToken", token.getToken());
        context.setVariable("fullName", user.getSurname() + " " + user.getName() + " " + user.getPatronymic());
        context.setVariable("organization", organization.getName());
        context.setVariable("email", user.getEmail());
        context.setVariable("phoneNumber", user.getPhoneNumber());
        context.setVariable("position", position.getName());

        String emailBody = engine.process("invitation_employee", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(emailBody, true);
        helper.setTo(employeeEmail);
        helper.setSubject("Приглашение в организацию");
        helper.setFrom("smart_tailor@gmail.com");
        return mimeMessage;
    }

    @Override
    public MimeMessage createMailWithConfirmationCode(AppUser user, ConfirmationCode confirmationCode) throws MessagingException {

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

    @Override
    public MimeMessage createSubscriptionRequestMail(AppUser user, SubscriptionToken token) throws MessagingException {

        Context context = new Context();
        context.setVariable("subscriptionToken", token.getToken());
        context.setVariable("fullName", user.getSurname() + " " + user.getName() + " " + user.getPatronymic());
        context.setVariable("email", user.getEmail());
        context.setVariable("phoneNumber", user.getPhoneNumber());

        String emailBody = engine.process("subscription_request", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(emailBody, true);
        helper.setTo("u11se03r@gmail.com");
        helper.setSubject("Запрос на подписку");
        helper.setFrom("smart_tailor@gmail.com");
        return mimeMessage;
    }

    @Override
    public void sendEmail(MimeMessage email) {
        javaMailSender.send(email);
    }

    @Override
    public void sendEmailWithConfirmationCode(ConfirmationCode confirmationCode, AppUser user) {

        if (confirmationCode != null)
            confirmationCodeService.delete(confirmationCode);

        confirmationCode = confirmationCodeService.generateConfirmationCode(user);
        MimeMessage simpleMailMessage;
        try {
            simpleMailMessage = createMailWithConfirmationCode(user, confirmationCode);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
        sendEmail(simpleMailMessage);
    }

    @Override
    public void sendEmailWithReceiptPDF(AppUser user, byte[] pdfFile) throws MessagingException, IOException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(user.getEmail());
        helper.setSubject("Квитанция на покупки");
        helper.setText("Вы можете найти квитанция для покупки внизу во вложениях.");
        helper.addAttachment("Receipt.pdf", new ByteArrayDataSource(new ByteArrayInputStream(pdfFile), "application/pdf"));
        javaMailSender.send(message);
    }
}