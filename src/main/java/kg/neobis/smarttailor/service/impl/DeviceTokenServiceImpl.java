package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.DeviceToken;
import kg.neobis.smarttailor.repository.DeviceTokenRepository;
import kg.neobis.smarttailor.service.DeviceTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceTokenServiceImpl implements DeviceTokenService {

    DeviceTokenRepository deviceTokenRepository;
    @Override
    @Transactional
    public void saveToken(String token) {
        DeviceToken deviceToken = new DeviceToken(token);
        deviceTokenRepository.save(deviceToken);
    }

    @Override
    public List<DeviceToken> findAll() {
        return deviceTokenRepository.findAll();
    }
}
