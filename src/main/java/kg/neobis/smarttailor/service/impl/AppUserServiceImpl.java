package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.CreateAdmin;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.enums.Role;
import kg.neobis.smarttailor.exception.NotAuthorizedException;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.repository.AppUserRepository;
import kg.neobis.smarttailor.service.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserServiceImpl implements AppUserService {

    AppUserRepository repository;
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> createAdmin(CreateAdmin request) {

        if (repository.existsUserByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("email is occupied", HttpStatus.CONFLICT.value());
        }

        AppUser user = AppUser.builder()
                .name(request.name())
                .surname(request.surname())
                .patronymic(request.patronymic())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .role(Role.ADMIN)
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .build();

        repository.save(user);

        return ResponseEntity.ok("successful registration!");
    }

    @Override
    public Boolean existsUserByEmail(String email) {
        return repository.existsUserByEmail(email);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public AppUser findUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Error: User not found with email: " + email, HttpStatus.NOT_FOUND.value()));
    }

    @Override
    public AppUser getUserFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AppUser appUser) {
                return appUser;
            } else {
                throw new IllegalArgumentException("Principal is not an instance of AppUser");
            }
        }
        throw new NotAuthorizedException("Authentication required!", HttpStatus.FORBIDDEN.value());
    }

    @Override
    public AppUser save(AppUser appUser) {
        return repository.save(appUser);
    }
}