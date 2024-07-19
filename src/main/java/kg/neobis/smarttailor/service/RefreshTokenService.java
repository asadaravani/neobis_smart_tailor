package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.RefreshToken;

public interface RefreshTokenService {

    void deleteExpiredTokens();

    Boolean existsByToken(String token);

    void save(RefreshToken refreshToken);
}