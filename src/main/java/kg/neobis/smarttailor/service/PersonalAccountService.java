package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.UserProfileDto;

public interface PersonalAccountService {

    UserProfileDto getUserProfile(String email);
}
