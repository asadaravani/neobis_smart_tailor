package kg.neobis.smarttailor.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloudinary")
@RequiredArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryConfig {

    @Value("${cloud_name}")
    String cloudName;

    @Value("${api_key}")
    String apiKey;

    @Value("${api_secret}")
    String apiSecret;
}
