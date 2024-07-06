package kg.neobis.smarttailor.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigInteger;

@Builder
public record ServiceAddRequest (
    String name,
    String description,
    BigInteger price,
    String contactInfo
)implements Serializable {}
