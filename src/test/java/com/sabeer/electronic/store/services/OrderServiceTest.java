package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.*;
import com.sabeer.electronic.store.entities.*;
import com.sabeer.electronic.store.exceptions.BadApiRequestException;
import com.sabeer.electronic.store.exceptions.ResourceNotFoundException;
import com.sabeer.electronic.store.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

@SpringBootTest
public class OrderServiceTest {

    // TODO:4 complete test cases for Order Service

    @Autowired
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper mapper;

    private User user;

    private Role role;

    private Product product;

    private Cart cart;

    private CartItem cartItem;

    private Order order;

    private OrderItem orderItem;

    @BeforeEach
    public void init() {
        role = Role.builder()
                .roleName("abc")
                .roleName("NORMAL")
                .build();
        user = User.builder()
                .name("Sabeer")
                .email("sabeer@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .roles(Set.of(role))
                .build();
        product = Product.builder()
                .productId("p123")
                .title("Redmi Note 5 Pro")
                .description("This is testing product")
                .price(20000)
                .discountedPrice(15000)
                .quantity(3)
                .live(true)
                .stock(true)
                .productImageName("product_abc.png")
                .build();
        cartItem = CartItem.builder()
                .totalPrice(45000)
                .quantity(3)
                .product(product)
                .build();
        cart = Cart.builder()
                .createdAt(new Date())
                .user(user)
                .items(new ArrayList<>(List.of(cartItem)))
                .build();
        orderItem = OrderItem.builder()
                .totalPrice(45000)
                .quantity(3)
                .product(product)
                .build();
        order = Order.builder()
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .orderedDate(new Date())
                .paymentStatus("PAID")
                .orderStatus("PENDING")
                .orderItems(new ArrayList<>(List.of(orderItem)))
                .user(user)
                .build();
    }

    // create order test
    @Test
    public void createOrderTest() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(cartRepository.findById(Mockito.any())).thenReturn(Optional.of(cart));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cart);
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);

        CreateOrderRequestDto createOrderRequestDto = CreateOrderRequestDto.builder()
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .userId("u123")
                .cartId("c123")
                .build();

        OrderDto orderDto = orderService.createOrder(createOrderRequestDto);

        Assertions.assertNotNull(orderDto);
        Assertions.assertEquals(order.getBillingName(), orderDto.getBillingName());
    }

    @Test
    public void createOrder_BadApiRequestException_Test() {
        Cart cart1 = Cart.builder()
                .createdAt(new Date())
                .user(user)
                .items(new ArrayList<>())
                .build();

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(cartRepository.findById(Mockito.any())).thenReturn(Optional.of(cart1));

        CreateOrderRequestDto createOrderRequestDto = CreateOrderRequestDto.builder()
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .userId("u123")
                .cartId("c123")
                .build();

        Assertions.assertThrows(BadApiRequestException.class, () -> orderService.createOrder(createOrderRequestDto));
    }

    // update order test
    @Test
    public void updateOrderTest() {
        String orderId = "o123";

        UpdateOrderRequestDto updateOrderRequestDto = UpdateOrderRequestDto.builder()
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .paymentStatus("PAID")
                .orderStatus("PENDING")
                .build();

        Mockito.when(orderRepository.findById(Mockito.anyString())).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);

        OrderDto orderDto = orderService.updateOrder(updateOrderRequestDto, orderId);

        Assertions.assertNotNull(orderDto);
        Assertions.assertEquals(order.getBillingName(), orderDto.getBillingName(), "Billing name is not matched !!");
    }

    @Test
    public void updateOrder_DeliveredOrder_Test() {
        String orderId = "o123";

        UpdateOrderRequestDto updateOrderRequestDto = UpdateOrderRequestDto.builder()
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .paymentStatus("PAID")
                .orderStatus("DELIVERED")
                .build();

        Mockito.when(orderRepository.findById(Mockito.anyString())).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);

        OrderDto orderDto = orderService.updateOrder(updateOrderRequestDto, orderId);

        Assertions.assertNotNull(orderDto);
        Assertions.assertEquals(order.getBillingName(), orderDto.getBillingName(), "Billing name is not matched !!");
    }

    @Test
    public void updateUser_ResourceNotFoundException_Test() {
        String orderId = "o123";

        UpdateOrderRequestDto updateOrderRequestDto = UpdateOrderRequestDto.builder()
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .paymentStatus("PAID")
                .orderStatus("DELIVERED")
                .build();

        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(updateOrderRequestDto, orderId));
    }

    // remove order test case
    @Test
    public void removeOrderTest() {
        Mockito.when(orderRepository.findById(Mockito.any())).thenReturn(Optional.of(order));

        orderService.removeOrder("o123");

        Mockito.verify(orderRepository, Mockito.times(1)).delete(Mockito.any());
    }

    // get orders of user test case
    @Test
    public void getOrdersOfUserTest() {
        Order order1 = Order.builder()
                .billingName("Hussain")
                .billingPhone("8508280103")
                .billingAddress("2B, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .orderedDate(new Date())
                .paymentStatus("NOT_PAID")
                .orderStatus("DISPATCHED")
                .user(user)
                .build();

        Order order2 = Order.builder()
                .billingName("Khader")
                .billingPhone("8508280104")
                .billingAddress("2C, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .orderedDate(new Date())
                .paymentStatus("PAID")
                .orderStatus("DELIVERED")
                .user(user)
                .build();

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(orderRepository.findByUser(Mockito.any())).thenReturn(new ArrayList<>(Arrays.asList(order, order1, order2)));

        List<OrderDto> orderDtoList = orderService.getOrdersOfUser("u123");

        Assertions.assertEquals(3, orderDtoList.size());
    }

    // get all orders of a user
    @Test
    public void getOrdersTest() {
        Order order1 = Order.builder()
                .billingName("Hussain")
                .billingPhone("8508280103")
                .billingAddress("2B, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .orderedDate(new Date())
                .paymentStatus("NOT_PAID")
                .orderStatus("DISPATCHED")
                .user(user)
                .build();

        Order order2 = Order.builder()
                .billingName("Khader")
                .billingPhone("8508280104")
                .billingAddress("2C, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .orderedDate(new Date())
                .paymentStatus("PAID")
                .orderStatus("DELIVERED")
                .user(user)
                .build();

        List<Order> orderList = List.of(order, order1, order2);
        Page<Order> page = new PageImpl<>(orderList);

        Mockito.when(orderRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<OrderDto> allOrders = orderService.getOrders(1, 2, "orderedDate", "asc");

        Assertions.assertEquals(3, allOrders.getContent().size());
    }

    @Test
    public void getOrders_SortByByOrderedDateInDescending_Test() {
        Order order1 = Order.builder()
                .billingName("Hussain")
                .billingPhone("8508280103")
                .billingAddress("2B, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .orderedDate(new Date())
                .paymentStatus("NOT_PAID")
                .orderStatus("DISPATCHED")
                .user(user)
                .build();

        Order order2 = Order.builder()
                .billingName("Khader")
                .billingPhone("8508280104")
                .billingAddress("2C, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .orderedDate(new Date())
                .paymentStatus("PAID")
                .orderStatus("DELIVERED")
                .user(user)
                .build();

        List<Order> orderList = List.of(order, order1, order2);
        Page<Order> page = new PageImpl<>(orderList);

        Mockito.when(orderRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<OrderDto> allOrders = orderService.getOrders(1, 2, "orderedDate", "desc");

        Assertions.assertEquals(3, allOrders.getContent().size());
    }
}
