package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrderDto;
import kg.neobis.smarttailor.dtos.OrderItemDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Order;

import kg.neobis.smarttailor.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public Order dtoToEntity(OrderDto requestDto, List<Image> orderImages, AppUser user) {

        List<OrderItem> items = requestDto.items().stream()
                .map(this::dtoToEntity).toList();

        return new Order(
                requestDto.name(),
                requestDto.description(),
                requestDto.dateOfExecution(),
                requestDto.contactInfo(),
                requestDto.price(),
                user,
                items,
                orderImages
        );
    }

    public OrderItem dtoToEntity(OrderItemDto dto) {
        OrderItem item = new OrderItem();
        item.setSize(dto.size());
        item.setQuantity(dto.quantity());
        return item;
    }
}