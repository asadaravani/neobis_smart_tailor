package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrderItemDto;
import kg.neobis.smarttailor.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItem dtoToEntity(OrderItemDto dto) {
        return new OrderItem(
                dto.size(),
                dto.quantity()
        );
    }

    public OrderItemDto entityToDto(OrderItem order) {
        return new OrderItemDto(
                order.getSize(),
                order.getQuantity()
        );
    }
}