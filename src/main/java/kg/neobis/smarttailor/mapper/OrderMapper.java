package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.entity.*;

import kg.neobis.smarttailor.enums.AdvertType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order dtoToEntity(OrderDto dto, List<Image> orderImages, AppUser user) {

        List<OrderItem> items = dto.items().stream()
                .map(orderItemDto -> new OrderItem(orderItemDto.size(), orderItemDto.quantity()))
                .toList();

        return new Order(
                dto.name(),
                dto.description(),
                dto.dateOfExecution(),
                dto.contactInfo(),
                dto.price(),
                user,
                items,
                orderImages
        );
    }

    public List<OrderListDto> entityListToDtoList(List<Order> orders) {
        return orders.stream().map(order -> new OrderListDto(
                order.getId(),
                order.getImages().get(1).getUrl(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getDateOfExecution()
        )).collect(Collectors.toList());
    }

    public OrderDetailsDto entityToOrderDetailsDto(Order order) {

        List<OrderItemDto> items = order.getItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem.getSize(), orderItem.getQuantity()))
                .toList();

        String imageUrl = null;
        if (order.getAuthor().getImage() != null)
            imageUrl = order.getAuthor().getImage().getUrl();

        return new OrderDetailsDto(
                order.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                order.getDateOfExecution(),
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                items,
                imageUrl,
                order.getAuthor().getSurname()
                        .concat(" ").concat(order.getAuthor().getName())
                        .concat(" ").concat(order.getAuthor().getPatronymic()),
                order.getContactInfo()
        );
    }

    public MyAdvertisement toMyAdvertisement(Order order){
        return MyAdvertisement.builder()
                .id(order.getId())
                .type(AdvertType.ORDER)
                .imagePath(order.getImages().get(0).getUrl())
                .name(order.getName())
                .description(order.getDescription())
                .createdAt(order.getCreatedAt())
                .build();
    }
}