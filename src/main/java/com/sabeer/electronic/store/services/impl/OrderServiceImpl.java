package com.sabeer.electronic.store.services.impl;

import com.sabeer.electronic.store.dtos.CreateOrderRequestDto;
import com.sabeer.electronic.store.dtos.OrderDto;
import com.sabeer.electronic.store.entities.*;
import com.sabeer.electronic.store.exceptions.BadApiRequestException;
import com.sabeer.electronic.store.exceptions.ResourceNotFoundException;
import com.sabeer.electronic.store.repositories.CartRepository;
import com.sabeer.electronic.store.repositories.OrderRepository;
import com.sabeer.electronic.store.repositories.UserRepository;
import com.sabeer.electronic.store.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderDto createOrder(CreateOrderRequestDto createOrderRequestDto) {
        String userId = createOrderRequestDto.getUserId();
        String cartId = createOrderRequestDto.getCartId();

        // fetch user
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));

        // fetch cart
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart with given id not found on server !!"));
        List<CartItem> cartItems = cart.getItems();

        if (cartItems.size() <= 0) {
            throw new BadApiRequestException("Invalid number of items in cart !!");
        }

        // other checks

        // create order
        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .billingName(createOrderRequestDto.getBillingName())
                .billingPhone(createOrderRequestDto.getBillingPhone())
                .billingAddress(createOrderRequestDto.getBillingAddress())
                .orderedDate(new Date())
                .deliveredDate(null)
                .paymentStatus(createOrderRequestDto.getPaymentStatus())
                .orderStatus(createOrderRequestDto.getOrderStatus())
                .user(user)
                .build();

        // orderItems, orderAmount

        AtomicReference<Integer> orderAmount = new AtomicReference<>(0);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
//            CartItem -> OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();

            orderAmount.set(orderAmount.get() + orderItem.getTotalPrice());

            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        // clear cart
        cart.getItems().clear();

        // save cart
        cartRepository.save(cart);

        //save order
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderDto.class);
    }
}
