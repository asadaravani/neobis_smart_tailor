package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrderItemDto;
import kg.neobis.smarttailor.dtos.ads.MyAdvertisement;
import kg.neobis.smarttailor.dtos.ads.detailed.OrderDetailed;
import kg.neobis.smarttailor.dtos.ads.list.OrderListDto;
import kg.neobis.smarttailor.dtos.ads.request.OrderRequestDto;
import kg.neobis.smarttailor.entity.*;

import kg.neobis.smarttailor.enums.AdvertType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order dtoToEntity(OrderRequestDto requestDto, List<Image> orderImages, AppUser user) {

        List<OrderItem> items = requestDto.items().stream()
                .map(orderItemDto -> new OrderItem(orderItemDto.size(), orderItemDto.quantity()))
                .toList();

        return new Order(
                requestDto.name(),
                requestDto.description(),
                requestDto.price(),
                requestDto.contactInfo(),
                requestDto.dateOfExecution(),
                orderImages,
                items,
                user
        );
    }

    public List<OrderListDto> entityListToDtoList(List<Order> orders) {
        return orders.stream().map(order -> new OrderListDto(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                getImageUrl(order.getImages(), 1),
                getFullName(order),
                getAuthorImageUrl(order)
        )).collect(Collectors.toList());
    }

    public OrderDetailed entityToDto(Order order) {

        List<OrderItemDto> items = order.getItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem.getSize(), orderItem.getQuantity()))
                .toList();

        return new OrderDetailed(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getContactInfo(),
                getAuthorImageUrl(order),
                getFullName(order),
                order.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                order.getDateOfExecution(),
                items
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

    private static String getAuthorImageUrl(Order order) {
        return (order.getAuthor() != null && order.getAuthor().getImage() != null) ?
                order.getAuthor().getImage().getUrl() : "";
    }

    private static String getFullName(Order order) {
        if (order == null || order.getAuthor() == null) {
            return "";
        }
        AppUser author = order.getAuthor();
        String name = author.getName() != null ? author.getName() : "";
        String surname = author.getSurname() != null ? author.getSurname() : "";
        String patronymic = author.getPatronymic() != null ? author.getPatronymic() : "";
        return (name + " " + surname + " " + patronymic).trim();
    }

    private static String getImageUrl(List<Image> images, int index) {
        return (images != null && images.size() > index) ? images.get(index).getUrl() : "";
    }
}