package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.repository.AppUserRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserServiceImpl implements AppUserService {
    AppUserRepository userRepository;
    AppUserMapper userMapper = AppUserMapper.INSTANCE;
    SubscriptionService subscriptionService;

    @Override
    public UserProfileDto getUserProfile(String email){
        AppUser user = findUserByEmail(email);
        UserProfileDto userDto = userMapper.appUserToUserProfileDto(user);
        userDto.setExpiryTime(subscriptionService.getSubscriptionExpiryTime(user));
        return userDto;
    }

    @Override
    public AppUser findUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Error: User not found with email: " + email, HttpStatus.NOT_FOUND.value()));
    }

}
