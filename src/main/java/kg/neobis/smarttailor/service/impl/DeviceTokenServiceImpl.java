package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.DeviceToken;
import kg.neobis.smarttailor.repository.DeviceTokenRepository;
import kg.neobis.smarttailor.service.DeviceTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceTokenServiceImpl implements DeviceTokenService {

    DeviceTokenRepository deviceTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(DeviceTokenService.class);
    @Override
    @Transactional
    public void saveToken(String token) {
        try {
            Optional<DeviceToken> deviceTokenFromRepo = deviceTokenRepository.findByToken(token);

            if (deviceTokenFromRepo.isEmpty()) {
                DeviceToken deviceToken = new DeviceToken(token);
                deviceTokenRepository.save(deviceToken);
                logger.info("Token saved successfully: {}", token);
            } else {
                logger.info("Token already exists: {}", token);
            }
        } catch (DataIntegrityViolationException e) {
            logger.warn("Token '{}' already exists in the database. Skipping save.", token);
        } catch (Exception e) {

            logger.error("Failed to save token: {}", token, e);
            throw e;
        }


    }

    @Override
    public List<DeviceToken> findAll() {
        return deviceTokenRepository.findAll();
    }
}
