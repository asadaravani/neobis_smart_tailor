package kg.neobis.smarttailor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kg.neobis.smarttailor.config.JwtUtil;
import kg.neobis.smarttailor.dtos.LoginAdmin;
import kg.neobis.smarttailor.dtos.LoginResponse;
import kg.neobis.smarttailor.dtos.SignUpRequest;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.enums.Role;
import kg.neobis.smarttailor.exception.InvalidRequestException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.service.AppUserService;
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

    AppUserService appUserService;
    AuthenticationManager authenticationManager;
    BlackListTokenService blackListTokenService;
    ConfirmationCodeService confirmationCodeService;
    EmailService emailService;
    JwtUtil jwtUtil;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseEntity<?> confirmEmail(String email, Integer code) {

        AppUser user = appUserService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: ".concat(email), HttpStatus.NOT_FOUND.value()));

        ConfirmationCode confirmationCode = confirmationCodeService.findByUserAndCode(user, code)
                .orElseThrow(() -> new ResourceNotFoundException("Confirmation code for user with email '".concat(email).concat("' wasn't found"), HttpStatus.NOT_FOUND.value()));

        if (confirmationCode.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Confirmation code has expired");
        }
        if (confirmationCode.getCode().equals(code)) {
            user.setEnabled(true);
            appUserService.save(user);
            confirmationCode.setConfirmedAt(LocalDateTime.now());
            confirmationCodeService.save(confirmationCode);
            var jwtToken = jwtUtil.generateToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid confirmation code");
    }

    @Override
    public ResponseEntity<?> login(String email) {

        var user = appUserService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email", HttpStatus.NOT_FOUND.value()));

        ConfirmationCode confirmationCode = confirmationCodeService.findConfirmationCodeByUser(user);
        if (confirmationCode != null) {
            confirmationCodeService.delete(confirmationCode);
        }

        confirmationCode = confirmationCodeService.generateConfirmationCode(user);
        MimeMessage simpleMailMessage;
        try {
            simpleMailMessage = emailService.createMailWithConfirmationCode(user, confirmationCode);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
        emailService.sendEmail(simpleMailMessage);

        return ResponseEntity.ok("confirmation code has been sent to the ".concat(email));
    }

    @Override
    public LoginResponse loginAdmin(LoginAdmin request) {

        if (request.email() == null || request.password() == null || request.email().isEmpty() || request.password().isEmpty()) {
            throw new InvalidRequestException("email and password are required", HttpStatus.BAD_REQUEST.value());
        }

        AppUser user = appUserService.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("invalid email or password", HttpStatus.NOT_FOUND.value()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResourceNotFoundException("invalid email or password", HttpStatus.NOT_FOUND.value());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.password()
                )
        );

        var jwtToken = jwtUtil.generateToken(user);
        return LoginResponse.builder()
                .token(jwtToken)
                .build();
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
    public void resendConfirmationCode(String email) {

        AppUser user = appUserService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Error: User not found with email: " + email, HttpStatus.NOT_FOUND.value()));

        ConfirmationCode confirmationCode = confirmationCodeService.findByUser(user)
                .orElse(null);

        if (confirmationCode != null) {
            confirmationCodeService.delete(confirmationCode);
        }
        confirmationCode = confirmationCodeService.generateConfirmationCode(user);

        MimeMessage simpleMailMessage;
        try {
            simpleMailMessage = emailService.createMailWithConfirmationCode(user, confirmationCode);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
        emailService.sendEmail(simpleMailMessage);
    }

    @Override
    @Transactional
    public ResponseEntity<?> signUp(SignUpRequest request) {
        AppUser user;
        if (!appUserService.existsUserByEmail(request.email())) {
            user = AppUser.builder()
                    .surname(request.surname())
                    .name(request.name())
                    .patronymic(request.patronymic())
                    .email(request.email())
                    .phoneNumber(request.phoneNumber())
                    .role(Role.USER)
                    .image(new Image("https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg"))
                    .enabled(false)
                    .hasSubscription(false)
                    .build();

            user = appUserService.save(user);
        } else {
            user = appUserService.findUserByEmail(request.email());
            if (user.isEnabled()) {
                throw new ResourceAlreadyExistsException("Error: Email is already in use!", HttpStatus.CONFLICT.value());
            } else {
                ConfirmationCode confirmationCode = confirmationCodeService.findConfirmationCodeByUser(user);
                if (confirmationCode != null) {
                    confirmationCodeService.delete(confirmationCode);
                }
            }
        }

        ConfirmationCode confirmationCode = confirmationCodeService.generateConfirmationCode(user);
        MimeMessage simpleMailMessage;
        try {
            simpleMailMessage = emailService.createMailWithConfirmationCode(user, confirmationCode);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
        emailService.sendEmail(simpleMailMessage);

        return ResponseEntity.ok("confirmation code has been sent to the ".concat(request.email()));
    }
}