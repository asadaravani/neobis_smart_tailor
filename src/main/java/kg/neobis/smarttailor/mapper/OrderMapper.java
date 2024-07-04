package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrderDetailsDto;
import kg.neobis.smarttailor.dtos.OrderDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Order;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    OrderItemMapper orderItemMapper;

    public Order dtoToEntity(OrderDto requestDto, List<Image> orderImages, AppUser user) {

        return new Order(
                requestDto.name(),
                requestDto.description(),
                requestDto.dateOfExecution(),
                requestDto.contactInfo(),
                requestDto.price(),
                user,
                requestDto.items().stream().map(orderItemMapper::dtoToEntity).toList(),
                orderImages
        );
    }

    public OrderDetailsDto entityToOrderDetailsDto(Order order) {
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
                order.getItems().stream().map(orderItemMapper::entityToDto).toList(),
                imageUrl,
                order.getAuthor().getSurname()
                        .concat(" ").concat(order.getAuthor().getName())
                        .concat(" ").concat(order.getAuthor().getPatronymic()),
                order.getContactInfo()
        );
    }
}