package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.SignUpRequest;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.enums.Role;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.repository.AppUserRepository;
import kg.neobis.smarttailor.repository.ConfirmationCodeRepository;
import kg.neobis.smarttailor.service.AuthenticationService;
import kg.neobis.smarttailor.service.ConfirmationCodeService;
import kg.neobis.smarttailor.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    AppUserRepository appUserRepository;
    ConfirmationCodeRepository confirmationCodeRepository;
    ConfirmationCodeService confirmationCodeService;
    EmailService emailService;

    @Override
    public ResponseEntity<?> confirmCode(String email, Integer code) {

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Error: User not found with email: " + email, HttpStatus.NOT_FOUND.value()));

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByUserAndCode(user, code)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Confirmation code for user wasn't found", HttpStatus.NOT_FOUND.value()));

        if (confirmationCode.getCode().equals(code)) {
            user.setEnabled(true);
            appUserRepository.save(user);
            confirmationCodeRepository.delete(confirmationCode);
            return ResponseEntity.ok("User successfully confirmed");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid confirmation code");
        }
    }

    @Override
    public ResponseEntity<?> signUp(SignUpRequest request) {

        if (appUserRepository.existsUserByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Error: Email is already in use!", HttpStatus.CONFLICT.value());
        }

        AppUser user = AppUser.builder()
                .surname(request.getSurname())
                .name(request.getName())
                .patronymic(request.getPatronymic())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .enabled(false)
                .build();

        user = appUserRepository.save(user);

        ConfirmationCode confirmationCode = confirmationCodeService.generateConfirmationCode(user);
        SimpleMailMessage simpleMailMessage = emailService.createMail(user, confirmationCode);
        emailService.sendEmail(simpleMailMessage);

        return ResponseEntity.ok("confirmation code has been sent to the ".concat(request.getEmail()));
    }

    @Override
    public void resendConfirmationCode(String email) {

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Error: User not found with email: " + email, HttpStatus.NOT_FOUND.value()));

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByUser(user)
                .orElse(null);

        if (confirmationCode == null) {
            confirmationCode = confirmationCodeService.generateConfirmationCode(user);
        }
        SimpleMailMessage simpleMailMessage = emailService.createMail(user, confirmationCode);
        emailService.sendEmail(simpleMailMessage);
    }
}