package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.*;
import kg.neobis.smarttailor.dtos.ads.order.OrderRequestDto;
import kg.neobis.smarttailor.entity.*;
import kg.neobis.smarttailor.enums.AdvertType;
import kg.neobis.smarttailor.enums.OrderStatus;
import kg.neobis.smarttailor.service.OrganizationEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final AppUserMapper appUserMapper;
    private final OrganizationEmployeeService organizationEmployeeService;

    public Order orderRequestDtoToEntity(OrderRequestDto requestDto, List<Image> orderImages, AppUser user) {

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
                .candidates(null)
                .organizationExecutor(null)
                .mainEmployeeExecutor(null)
                .build();
    }

    public AdvertisementListDto entityToAdvertisementListDto(Order order) {
        return new AdvertisementListDto(
                AdvertType.ORDER,
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getFirstImage(order.getImages()),
                order.getFullName(order),
                order.getAuthorImageUrl(order),
                order.getUpdatedAt()
        );
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

    public CurrentOrderDetailed entityToCurrentOrderDetailed(Order order) {
        return CurrentOrderDetailed.builder()
                .id(order.getId())
                .name(order.getName())
                .description(order.getDescription())
                .price(order.getPrice())
                .status(order.getStatus())
                .dateOfExecution(order.getDateOfExecution())
                .images(order.getImages().stream().map(Image::getUrl).collect(Collectors.toList()))
                .employees(order.getOrderEmployees().stream()
                        .map(appUserMapper::entityToEmployeeDto)
                        .collect(Collectors.toList()))
                .authorFullName(order.getFullName(order))
                .authorImage(order.getAuthorImageUrl(order))
                .authorContactInfo(order.getContactInfo())
                .build();
    }

    public List<EmployeeStageOrderListDto> entityListToEmployeeStageOrderListDto(Page<Order> orders, String stage) {
        return orders.stream().map(order -> EmployeeStageOrderListDto.builder()
                        .id(order.getId())
                        .name(order.getName())
                        .description(order.getDescription())
                        .price(order.getPrice())
                        .date(stage.equals("completed") ? order.getDateOfCompletion() : order.getDateOfStart())
                        .employees(order.getOrderEmployees().stream()
                                .map(appUserMapper::entityToEmployeeDto)
                                .collect(Collectors.toList()))
                        .authorFullName(order.getFullName(order))
                        .authorImage(order.getAuthorImageUrl(order))
                        .authorContactInfo(order.getContactInfo())
                        .build())
                .collect(Collectors.toList());
    }

    public List<OrganizationOrderHistoryPage> entityListToOrganizationOrderHistoryPage(Page<Order> orders, String stage) {
        return orders.stream().map(order -> OrganizationOrderHistoryPage.builder()
                        .id(order.getId())
                        .name(order.getName())
                        .description(order.getDescription())
                        .orderImage(order.getFirstImage(order.getImages()))
                        .orderStatus(order.getStatus())
                        .date(stage.equals("current") ? order.getDateOfStart() : order.getDateOfExecution())
                        .build())
                .collect(Collectors.toList());
    }

    public List<UserOrderHistoryDto> entityListToUserOrderHistoryDto(Page<Order> orders, String stage) {
        return orders.stream().map(order -> UserOrderHistoryDto.builder()
                        .id(order.getId())
                        .name(order.getName())
                        .price(order.getPrice())
                        .status(order.getStatus())
                        .date(stage.equals("completed") ? order.getDateOfCompletion() : order.getDateOfStart())
                        .build())
                .collect(Collectors.toList());
    }

    public List<OrganizationOrdersDto> entityListToOrganizationOrderListDto(List<Order> orders) {
        return orders.stream().map(order -> OrganizationOrdersDto.builder()
                        .id(order.getId())
                        .name(order.getName())
                        .description(order.getDescription())
                        .price(order.getPrice())
                        .imageUrl(order.getImages().get(0).getUrl())
                        .status(order.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    public AuthorOrderDetailedDto entityToAuthorOrderDetailedDto(Order order) {

        List<OrderItemDto> items = order.getItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem.getSize(), orderItem.getQuantity()))
                .toList();

        List<CandidateDto> candidates = order.getCandidates().stream()
                .map(user -> new CandidateDto(
                        user.getId(),
                        String.format("%s %s %s", user.getSurname(), user.getName(), user.getPatronymic()),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        organizationEmployeeService.findByEmployeeEmail(user.getEmail()).getOrganization().getName()
                ))
                .toList();

        AppUser mainExecutor = order.getMainEmployeeExecutor();

        CandidateDto executor = null;
        if (order.getMainEmployeeExecutor() != null) {
            executor = new CandidateDto(
                    mainExecutor.getId(),
                    mainExecutor.getFullName(),
                    mainExecutor.getEmail(),
                    mainExecutor.getPhoneNumber(),
                    order.getOrganizationExecutor().getName()
            );
        }

        return new AuthorOrderDetailedDto(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getContactInfo(),
                order.getAuthorImageUrl(order),
                order.getFullName(order),
                order.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                order.getDateOfExecution(),
                order.getStatus(),
                items,
                candidates,
                executor
        );
    }

    public OrderDetailed entityToOrderDetailed(Order order) {

        List<OrderItemDto> items = order.getItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem.getSize(), orderItem.getQuantity()))
                .toList();

        return new OrderDetailed(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getContactInfo(),
                order.getStatus(),
                order.getAuthorImageUrl(order),
                order.getFullName(order),
                order.getImages().stream().map(Image::getUrl).collect(Collectors.toList()),
                order.getDateOfExecution(),
                items
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
                order.getName(),
                order.getDescription(),
                order.getDateOfStart()
        );
    }

    public OrganizationOrdersDto toOrganizationOrders(Order order) {
        return new OrganizationOrdersDto(
                order.getId(),
                order.getName(),
                order.getDescription(),
                order.getPrice(),
                order.getImages().get(0).getUrl(),
                order.getStatus()
        );
    }
}