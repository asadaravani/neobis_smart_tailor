package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.DeviceToken;

import java.util.List;

public interface DeviceTokenService {
    void saveToken(String token);

    List<DeviceToken> findAll();
}
