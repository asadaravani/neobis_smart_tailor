package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kg.neobis.smarttailor.entity.*;

import java.io.IOException;

public interface EmailService {

    MimeMessage createInvitationEmployeeMail(AppUser user, String employeeEmail, InvitationToken token, Organization organization, Position position) throws MessagingException;

    MimeMessage createMailWithConfirmationCode(AppUser user, ConfirmationCode confirmationCode) throws MessagingException;

    MimeMessage createSubscriptionRequestMail(AppUser user, SubscriptionToken token) throws MessagingException;

    void sendEmail(MimeMessage email);

    void sendEmailWithConfirmationCode(ConfirmationCode confirmationCode, AppUser user);

    void sendEmailWithReceiptPDF(AppUser user, byte[] pdfFile) throws MessagingException, IOException;
}