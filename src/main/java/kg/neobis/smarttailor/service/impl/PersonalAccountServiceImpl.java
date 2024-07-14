package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.mapper.AppUserMapper;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.PersonalAccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonalAccountServiceImpl implements PersonalAccountService {

    AppUserService appUserService;
    AppUserMapper userMapper = AppUserMapper.INSTANCE;

    @Override
    public UserProfileDto getUserProfile(String email) {
        AppUser user = appUserService.findUserByEmail(email);
        return userMapper.appUserToUserProfileDto(user);
    }
}