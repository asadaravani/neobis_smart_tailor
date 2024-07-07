package kg.neobis.smarttailor.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;

import java.io.IOException;

public interface EmailService {

    void sendEmail(MimeMessage email);

    MimeMessage createMail(AppUser user, ConfirmationCode confirmationCode) throws MessagingException;

    void sendEmailWithReceiptPDF(AppUser user, byte[] pdfFile) throws MessagingException, IOException;
}