package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.BlackListToken;
import kg.neobis.smarttailor.repository.BlackListTokenRepository;
import kg.neobis.smarttailor.service.BlackListTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlackListTokenServiceImpl implements BlackListTokenService {

    BlackListTokenRepository blackListTokenRepository;

    @Override
    public void addTokenToBlacklist(String token) {
        BlackListToken blacklistToken = new BlackListToken();
        blacklistToken.setToken(token);
        blackListTokenRepository.save(blacklistToken);
    }

    @Override
    public void deleteAll() {
        blackListTokenRepository.deleteAll();
    }

    public boolean isTokenBlacklisted(String token) {
        return blackListTokenRepository.existsByToken(token);
    }
}