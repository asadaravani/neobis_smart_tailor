package kg.neobis.smarttailor.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kg.neobis.smarttailor.config.JwtUtil;
import kg.neobis.smarttailor.dtos.AccessToken;
import kg.neobis.smarttailor.dtos.LoginResponse;
import kg.neobis.smarttailor.dtos.SignUpRequest;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.RefreshToken;
import kg.neobis.smarttailor.enums.Role;
import kg.neobis.smarttailor.exception.OutOfDateException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.AuthenticationService;
import kg.neobis.smarttailor.service.BlackListTokenService;
import kg.neobis.smarttailor.service.ConfirmationCodeService;
import kg.neobis.smarttailor.service.EmailService;
import kg.neobis.smarttailor.service.RefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    AppUserService appUserService;
    BlackListTokenService blackListTokenService;
    ConfirmationCodeService confirmationCodeService;
    EmailService emailService;
    JwtUtil jwtUtil;
    RefreshTokenService refreshTokenService;
    UserDetailsService userDetailsService;

    @Override
    @Transactional
    public LoginResponse confirmEmail(String requestEmail, Integer requestCode) {

        AppUser user = appUserService.findByEmail(requestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email \"".concat(requestEmail).concat("\"")));
        ConfirmationCode confirmationCode = confirmationCodeService.findByUserAndCode(user, requestCode)
                .orElseThrow(() -> new ResourceNotFoundException("Confirmation code not found for user with email \"".concat(requestEmail).concat("\"")));

        if (confirmationCode.isExpired()) {
            throw new OutOfDateException("Confirmation code has expired");
        }

        if (confirmationCode.getCode().equals(requestCode)) {
            user.setEnabled(true);
            appUserService.save(user);
            confirmationCodeService.delete(confirmationCode);

            var jwtToken = jwtUtil.generateToken(user);
            var refreshToken = jwtUtil.generateRefreshToken(user);

            RefreshToken refreshTokenToSave = RefreshToken.builder()
                    .token(refreshToken)
                    .build();
            refreshTokenService.save(refreshTokenToSave);

            return LoginResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            throw new ResourceNotFoundException("Invalid confirmation code");
        }
    }

    @Override
    public String login(String requestEmail) {
        AppUser user = appUserService.findByEmail(requestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email \"".concat(requestEmail).concat("\"")));
        ConfirmationCode confirmationCode = confirmationCodeService.findConfirmationCodeByUser(user);
        emailService.sendEmailWithConfirmationCode(confirmationCode, user);

        return "Confirmation code has been sent to the ".concat(requestEmail);
    }

    @Override
    public String logOut(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            blackListTokenService.addTokenToBlacklist(jwtToken);
            return "Log out completed";
        }
        return "Invalid authorization header";
    }

    @Override
    public AccessToken refreshToken(String refreshToken) {

        if (!refreshTokenService.existsByToken(refreshToken))
            throw new ResourceNotFoundException("Refresh token not found");
        if (jwtUtil.isRefreshTokenExpired(refreshToken)) {
            refreshTokenService.deleteExpiredTokens();
            throw new OutOfDateException("Refresh token has expired");
        }
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateToken(userDetails);

        return AccessToken.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public String resendConfirmationCode(String requestEmail) {

        AppUser user = appUserService.findByEmail(requestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email \n".concat(requestEmail).concat("\"")));
        ConfirmationCode confirmationCode = confirmationCodeService.findByUser(user)
                .orElse(null);
        emailService.sendEmailWithConfirmationCode(confirmationCode, user);

        return "Confirmation code has been resent to the ".concat(requestEmail);
    }

    @Override
    @Transactional
    public String signUp(SignUpRequest request) {

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
                throw new ResourceAlreadyExistsException("User with email \"".concat(request.email()).concat("\" is already exists"));
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

        return "Confirmation code has been sent to the ".concat(request.email());
    }
}