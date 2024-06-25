package kg.neobis.smarttailor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import kg.neobis.smarttailor.config.JwtUtil;
import kg.neobis.smarttailor.dtos.AddAdminRequest;
import kg.neobis.smarttailor.dtos.LogInRequest;
import kg.neobis.smarttailor.dtos.LogInResponse;
import kg.neobis.smarttailor.dtos.SignUpRequest;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.enums.Role;
import kg.neobis.smarttailor.exception.InvalidRequestException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.repository.AppUserRepository;
import kg.neobis.smarttailor.repository.ConfirmationCodeRepository;
import kg.neobis.smarttailor.service.AuthenticationService;
import kg.neobis.smarttailor.service.BlackListTokenService;
import kg.neobis.smarttailor.service.ConfirmationCodeService;
import kg.neobis.smarttailor.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    AppUserRepository appUserRepository;
    AuthenticationManager authenticationManager;
    BlackListTokenService blackListTokenService;
    ConfirmationCodeRepository confirmationCodeRepository;
    ConfirmationCodeService confirmationCodeService;
    EmailService emailService;
    JwtUtil jwtUtil;
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> addAdmin(AddAdminRequest request) {

        if (appUserRepository.existsUserByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("email is occupied", HttpStatus.CONFLICT.value());
        }

        AppUser user = AppUser.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .patronymic(request.getPatronymic())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ADMIN)
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        appUserRepository.save(user);

        return ResponseEntity.ok("successful registration!");
    }
    @Override
    public ResponseEntity<?> confirmEmail(String email, Integer code) {

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Error: User not found with email: " + email, HttpStatus.NOT_FOUND.value()));

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByUserAndCode(user, code)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Confirmation code for user wasn't found", HttpStatus.NOT_FOUND.value()));

        if (confirmationCode.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Confirmation code has expired");
        }
        if (confirmationCode.getCode().equals(code)) {
            user.setEnabled(true);
            appUserRepository.save(user);
            confirmationCode.setConfirmedAt(LocalDateTime.now());
            confirmationCodeRepository.save(confirmationCode);

            var jwtToken = jwtUtil.generateToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid confirmation code");
    }

    @Override
    public ResponseEntity<?> logIn(String email) {

        var user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email", HttpStatus.NOT_FOUND.value()));

        ConfirmationCode confirmationCode = confirmationCodeRepository.findConfirmationCodeByUser(user);
        if (confirmationCode != null) {
            confirmationCodeRepository.delete(confirmationCode);
        }

        confirmationCode = confirmationCodeService.generateConfirmationCode(user);
        MimeMessage simpleMailMessage;
        try {
            simpleMailMessage = emailService.createMail(user, confirmationCode);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
        emailService.sendEmail(simpleMailMessage);

        return ResponseEntity.ok("confirmation code has been sent to the ".concat(email));
    }

    @Override
    public ResponseEntity<?> logOut(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            blackListTokenService.addTokenToBlacklist(jwtToken);
            return ResponseEntity.ok("successful log out!");
        }
        return ResponseEntity.badRequest().body("invalid authorization header");
    }


    @Override
    public LogInResponse logInAdmin(LogInRequest request) {

        if (request.getEmail() == null || request.getPassword() == null || request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            throw new InvalidRequestException("email and password are required", HttpStatus.BAD_REQUEST.value());
        }

        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("invalid email or password", HttpStatus.NOT_FOUND.value()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("invalid email or password", HttpStatus.NOT_FOUND.value());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtUtil.generateToken(user);
        return LogInResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public void resendConfirmationCode(String email) {

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Error: User not found with email: " + email, HttpStatus.NOT_FOUND.value()));

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByUser(user)
                .orElse(null);

        if (confirmationCode != null) {
            confirmationCodeRepository.delete(confirmationCode);
        }
        confirmationCode = confirmationCodeService.generateConfirmationCode(user);

        MimeMessage simpleMailMessage;
        try {
            simpleMailMessage = emailService.createMail(user, confirmationCode);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
        emailService.sendEmail(simpleMailMessage);
    }

    @Override
    public ResponseEntity<?> signUp(SignUpRequest request) {

        AppUser user;
        if (!appUserRepository.existsUserByEmail(request.getEmail())) {
            user = AppUser.builder()
                    .surname(request.getSurname())
                    .name(request.getName())
                    .patronymic(request.getPatronymic())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .role(Role.USER)
                    .enabled(false)
                    .build();

            user = appUserRepository.save(user);
        } else {
            user = appUserRepository.findUserByEmail(request.getEmail());
            if (user.isEnabled()) {
                throw new ResourceAlreadyExistsException("Error: Email is already in use!", HttpStatus.CONFLICT.value());
            } else {
                ConfirmationCode confirmationCode = confirmationCodeRepository.findConfirmationCodeByUser(user);
                if (confirmationCode != null) {
                    confirmationCodeRepository.delete(confirmationCode);
                }
            }
        }

        ConfirmationCode confirmationCode = confirmationCodeService.generateConfirmationCode(user);
        MimeMessage simpleMailMessage;
        try {
            simpleMailMessage = emailService.createMail(user, confirmationCode);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
        emailService.sendEmail(simpleMailMessage);

        return ResponseEntity.ok("confirmation code has been sent to the ".concat(request.getEmail()));
    }
}