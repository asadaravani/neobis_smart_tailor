package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.util.List;

public record PositionsWeightGroups(
        List<PositionCard> five,
        List<PositionCard> four,
        List<PositionCard> three,
        List<PositionCard> two,
        List<PositionCard> one
) implements Serializable {}