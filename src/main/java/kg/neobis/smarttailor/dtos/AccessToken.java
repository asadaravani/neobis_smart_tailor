package kg.neobis.smarttailor.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AccessToken(@JsonProperty String accessToken) {}