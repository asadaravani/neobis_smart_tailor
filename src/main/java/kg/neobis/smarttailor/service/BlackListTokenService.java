package kg.neobis.smarttailor.service;

public interface BlackListTokenService {

    void addTokenToBlacklist(String token);

    void deleteAll();

    boolean isTokenBlacklisted(String token);
}