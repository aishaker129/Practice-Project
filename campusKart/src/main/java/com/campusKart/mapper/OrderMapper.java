package com.campusKart.mapper;

import com.campusKart.entity.OrderItem;
import com.campusKart.entity.Orders;
import com.campusKart.entity.Product;
import com.campusKart.entity.record.BuyerDTO;
import com.campusKart.entity.record.OrderItemDTO;
import com.campusKart.entity.record.OrderResponseDTO;
import com.campusKart.entity.record.SimpleUserDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
//
//@Component
//public class OrderMapper {
//
//    public  OrderResponseDTO toDTO(Orders order) {
//        BuyerDTO buyerDTO = new BuyerDTO(
//                order.getBuyer().getId(),
//                order.getBuyer().getName(),
//                order.getBuyer().getEmail()
//        );
//
//        List<OrderItemDTO> items = order.getItems().stream()
//                .map(OrderMapper::toOrderItemDTO)
//                .collect(Collectors.toList());
//
//        double totalAmount = order.getItems().stream()
//                .mapToDouble(item -> item.getTotalPrice() * item.getQuantity())
//                .sum();
//
//        return new OrderResponseDTO(
//                order.getId(),
//                buyerDTO,
//                order.getStatus().name(),
//                order.getCreatedAt(),
//                items,
//                totalAmount
//        );
//    }
//
//    private  OrderItemDTO toOrderItemDTO(OrderItem item) {
//        Product p = item.getProduct();
//        SimpleUserDTO postedBy = new SimpleUserDTO(
//                p.getPostedBy().getId(),
//                p.getPostedBy().getName()
//        );
//
//        return new OrderItemDTO(
//                p.getId(),
//                p.getTitle(),
//                item.getTotalPrice(),
//                item.getQuantity(),
//                p.getProductCategory(),
//                postedBy
//        );
//    }
//}

@Component
public class OrderMapper {

    public OrderResponseDTO toDTO(Orders order) {
        BuyerDTO buyerDTO = new BuyerDTO(
                order.getBuyer().getId(),
                order.getBuyer().getName(),
                order.getBuyer().getEmail()
        );

        List<OrderItemDTO> items = order.getItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());

        double totalAmount = order.getItems().stream()
                .mapToDouble(item -> item.getTotalPrice() * item.getQuantity())
                .sum();

        return new OrderResponseDTO(
                order.getId(),
                buyerDTO,
                order.getStatus().name(),
                order.getCreatedAt(),
                items,
                totalAmount
        );
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        Product p = item.getProduct();
        SimpleUserDTO postedBy = new SimpleUserDTO(
                p.getPostedBy().getId(),
                p.getPostedBy().getName()
        );

        return new OrderItemDTO(
                p.getId(),
                p.getTitle(),
                item.getTotalPrice(),
                item.getQuantity(),
                p.getProductCategory(),
                postedBy
        );
    }
}
