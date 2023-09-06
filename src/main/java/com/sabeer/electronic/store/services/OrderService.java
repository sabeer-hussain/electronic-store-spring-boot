package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.CreateOrderRequestDto;
import com.sabeer.electronic.store.dtos.OrderDto;

public interface OrderService {

    // create order
    OrderDto createOrder(CreateOrderRequestDto createOrderRequestDto);
}
