package kg.neobis.smarttailor.dtos;

public record MyAdvertisementCard(
        Long id,
        String imageUrl,
        String type,
        String name,
        String description
) {
}
