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
                .collect(Collectors.toList());

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
                getImageUrl(order.getImages(), 1),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getDateOfExecution()
        )).collect(Collectors.toList());
    }

    public OrderDetailsDto entityToOrderDetailsDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem.getSize(), orderItem.getQuantity()))
                .collect(Collectors.toList());

        String authorImageUrl = getAuthorImageUrl(order);

        return new OrderDetailsDto(
                order.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                order.getDateOfExecution(),
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                items,
                authorImageUrl,
                formatAuthorFullName(order.getAuthor()),
                order.getContactInfo()
        );
    }

    public MyAdvertisement toMyAdvertisement(Order order) {
        return MyAdvertisement.builder()
                .id(order.getId())
                .type(AdvertType.ORDER)
                .imagePath(getImageUrl(order.getImages(), 0))
                .name(order.getName())
                .description(order.getDescription())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private static String getImageUrl(List<Image> images, int index) {
        return (images != null && images.size() > index) ? images.get(index).getUrl() : "";
    }

    private static String getAuthorImageUrl(Order order) {
        return (order.getAuthor() != null && order.getAuthor().getImage() != null) ?
                order.getAuthor().getImage().getUrl() : "";
    }

    private static String formatAuthorFullName(AppUser author) {
        if (author == null) {
            return "";
        }
        String name = author.getName() != null ? author.getName() : "";
        String surname = author.getSurname() != null ? author.getSurname() : "";
        String patronymic = author.getPatronymic() != null ? author.getPatronymic() : "";
        return (surname + " " + name + " " + patronymic).trim();
    }
}