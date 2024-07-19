package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.RefreshToken;
import kg.neobis.smarttailor.repository.RefreshTokenRepository;
import kg.neobis.smarttailor.service.RefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {

    RefreshTokenRepository refreshTokenRepository;

    @Override
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpirationTimeBefore(LocalDateTime.now());
    }

    @Override
    public Boolean existsByToken(String token) {
        return refreshTokenRepository.existsByToken(token);
    }

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }
}