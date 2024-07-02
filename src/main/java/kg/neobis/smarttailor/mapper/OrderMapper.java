package kg.neobis.smarttailor.mapper;

import kg.neobis.smarttailor.dtos.OrderDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.entity.Order;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public Order dtoToEntity(OrderDto requestDto, List<Image> orderImages, AppUser user) {
        return new Order(
                requestDto.name(),
                requestDto.description(),
                orderImages,
                requestDto.sizes(),
                requestDto.termOfExecution(),
                requestDto.contactInfo(),
                requestDto.price(),
                user
        );
    }
}