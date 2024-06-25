package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.UserProfileDto;
import kg.neobis.smarttailor.entity.AppUser;

public interface AppUserService {
    UserProfileDto getUserProfile(String email);

    AppUser findUserByEmail(String email);
}
