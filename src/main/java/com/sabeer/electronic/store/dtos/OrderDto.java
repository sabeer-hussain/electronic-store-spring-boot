package com.sabeer.electronic.store.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDto {
    private String orderId;
    private String orderStatus = "PENDING";
    private String paymentStatus = "NOT_PAID";
    private int orderAmount;
    private String billingAddress;
    private String billingPhone;
    private String billingName;
    private Date orderedDate = new Date();
    private Date deliveredDate;
    // add this to get user information with order
    private UserDto user;
    private List<OrderItemDto> orderItems = new ArrayList<>();
}
