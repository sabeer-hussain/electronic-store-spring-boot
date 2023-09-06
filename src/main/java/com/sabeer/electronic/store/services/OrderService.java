package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.CreateOrderRequestDto;
import com.sabeer.electronic.store.dtos.OrderDto;
import com.sabeer.electronic.store.dtos.PageableResponse;

import java.util.List;

public interface OrderService {

    // create order
    OrderDto createOrder(CreateOrderRequestDto createOrderRequestDto);

    // remove order
    void removeOrder(String orderId);

    // get orders of user
    List<OrderDto> getOrdersOfUser(String userId);

    // get orders
    PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir);
}
