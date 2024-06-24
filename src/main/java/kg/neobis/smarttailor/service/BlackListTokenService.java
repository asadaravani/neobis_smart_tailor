package kg.neobis.smarttailor.service;

public interface BlackListTokenService {

    void addTokenToBlacklist(String token);

    boolean isTokenBlacklisted(String token);
}