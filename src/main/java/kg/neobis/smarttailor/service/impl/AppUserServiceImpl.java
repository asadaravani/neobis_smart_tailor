package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.CreateAdmin;
import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.enums.Role;
import kg.neobis.smarttailor.exception.ResourceAlreadyExistsException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.repository.AppUserRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserServiceImpl implements AppUserService {

    AppUserRepository repository;
    AppUserMapper userMapper = AppUserMapper.INSTANCE;
    PasswordEncoder passwordEncoder;
    SubscriptionService subscriptionService;

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

    public AppUser findUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Error: User not found with email: " + email, HttpStatus.NOT_FOUND.value()));
    }

    @Override
    public UserProfileDto getUserProfile(String email) {
        AppUser user = findUserByEmail(email);
        UserProfileDto userDto = userMapper.appUserToUserProfileDto(user);
        userDto.setExpiryTime(subscriptionService.getSubscriptionExpiryTime(user));
        return userDto;
    }

    @Override
    public AppUser save(AppUser appUser) {
        return repository.save(appUser);
    }
}