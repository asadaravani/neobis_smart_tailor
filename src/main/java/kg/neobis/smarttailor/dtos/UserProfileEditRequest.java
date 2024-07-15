package kg.neobis.smarttailor.dtos;

import lombok.Builder;
import java.io.Serializable;

@Builder
public record UserProfileEditRequest(
) implements Serializable { }
