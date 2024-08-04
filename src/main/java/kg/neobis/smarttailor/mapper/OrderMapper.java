package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrderCard;
import kg.neobis.smarttailor.dtos.OrderItemDto;
import kg.neobis.smarttailor.dtos.OrganizationDto;
import kg.neobis.smarttailor.dtos.OrganizationOrders;
import kg.neobis.smarttailor.dtos.ads.MyAdvertisement;
import kg.neobis.smarttailor.dtos.ads.detailed.OrderDetailed;
import kg.neobis.smarttailor.dtos.ads.list.OrderListDto;
import kg.neobis.smarttailor.dtos.ads.request.OrderRequestDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Order;
import kg.neobis.smarttailor.entity.OrderItem;

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

        return Order.builder()
                .name(requestDto.name())
                .description(requestDto.description())
                .price(requestDto.price())
                .contactInfo(requestDto.contactInfo())
                .isVisible(true)
                .dateOfExecution(requestDto.dateOfExecution())
                .dateOfStart(null)
                .dateOfCompletion(null)
                .status(OrderStatus.NOT_CONFIRMED)
                .author(user)
                .images(orderImages)
                .items(items)
                .organizationCandidates(null)
                .organizationExecutor(null)
                .build();
    }

    public List<OrderListDto> entityListToDtoList(List<Order> orders) {
        return orders.stream().map(order -> new OrderListDto(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getDateOfExecution(),
                order.getFirstImage(order.getImages()),
                order.getFullName(order),
                order.getAuthorImageUrl(order)
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
                order.getAuthorImageUrl(order),
                order.getFullName(order),
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
                .imagePath(order.getFirstImage(order.getImages()))
                .name(order.getName())
                .description(order.getDescription())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public OrderCard toOrderCard(Order order) {
        return new OrderCard(
                order.getId(),
                order.getDescription(),
                order.getDateOfStart()
        );
    }

    public OrganizationOrders toOrganizationOrders(Order order) {
        return new OrganizationOrders(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getImages().get(0).getUrl()
        );
    }
}