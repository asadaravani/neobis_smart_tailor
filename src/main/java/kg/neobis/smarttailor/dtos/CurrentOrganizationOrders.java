package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.util.List;

public record CurrentOrganizationOrders(
        List<OrderCard> notConfirmed,
        List<OrderCard> waiting,
        List<OrderCard> inProgress,
        List<OrderCard> checking,
        List<OrderCard> sending,
        List<OrderCard> arrived
) implements Serializable {}