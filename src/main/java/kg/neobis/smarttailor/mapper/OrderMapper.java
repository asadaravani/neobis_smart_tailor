package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrderCard;
import kg.neobis.smarttailor.dtos.OrderItemDto;
import kg.neobis.smarttailor.dtos.OrganizationDto;
import kg.neobis.smarttailor.dtos.OrganizationOrders;
import kg.neobis.smarttailor.dtos.ads.MyAdvertisement;
import kg.neobis.smarttailor.dtos.ads.detailed.OrderDetailed;
import kg.neobis.smarttailor.dtos.ads.list.OrderListDto;
import kg.neobis.smarttailor.dtos.ads.request.OrderRequestDto;
import kg.neobis.smarttailor.entity.*;

import kg.neobis.smarttailor.enums.AdvertType;
import kg.neobis.smarttailor.enums.OrderStatus;
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
                null,
                null,
                OrderStatus.NULL,
                null,
                orderImages,
                items,
                null,
                user
        );
    }

    public List<OrderListDto> entityListToDtoList(List<Order> orders) {
        return orders.stream().map(order -> new OrderListDto(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getDateOfExecution(),
                getImageUrl(order.getImages(), 0),
                getFullName(order),
                getAuthorImageUrl(order)
        )).collect(Collectors.toList());
    }

    public OrderDetailed entityToDto(Order order, boolean isAuthor) {

        OrderStatus status = order.getStatus();
        List<OrganizationDto> candidates;

        if (isAuthor) {
            candidates = order.getOrganizationCandidates().stream()
                    .map(organization -> new OrganizationDto(organization.getName(), organization.getDescription()))
                    .toList();
        } else {
            candidates = null;
            status = null;
        }

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
                status,
                items,
                candidates
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

    public OrganizationOrders toOrganizationOrders(Order order){
        return new OrganizationOrders(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getImages().get(0).getUrl()
        );
    }

    public OrderCard toOrderCard(Order order){
        return new OrderCard(
                order.getId(),
                order.getDescription(),
                order.getDateOfStart()
        );
    }
}