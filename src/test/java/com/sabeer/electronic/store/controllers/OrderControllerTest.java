package com.sabeer.electronic.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabeer.electronic.store.dtos.*;
import com.sabeer.electronic.store.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @MockBean
    private OrderService orderService;

    private ProductDto productDto;

    private OrderDto orderDto;

    private OrderItemDto orderItemDto;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        productDto = ProductDto.builder()
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
        orderItemDto = OrderItemDto.builder()
                .orderItemId(123)
                .totalPrice(45000)
                .quantity(3)
                .product(productDto)
                .build();
        orderDto = OrderDto.builder()
                .orderId("o123")
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .orderedDate(new Date())
                .paymentStatus("PAID")
                .orderStatus("PENDING")
                .orderItems(new ArrayList<>(List.of(orderItemDto)))
                .build();
    }

    @Test
    public void createOrderTest() throws Exception {
        Mockito.when(orderService.createOrder(Mockito.any())).thenReturn(orderDto);

        CreateOrderRequestDto createOrderRequestDto = CreateOrderRequestDto.builder()
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .userId("u123")
                .cartId("c123")
                .build();

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(createOrderRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists());
    }

    @Test
    public void removeOrderTest() throws Exception {
        String orderId = "o123";
        Mockito.doNothing().when(orderService).removeOrder(Mockito.anyString());

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/orders/" + orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void getOrdersOfUserTest() throws Exception {
        String orderId = "123";
        Mockito.when(orderService.getOrdersOfUser(Mockito.anyString())).thenReturn(List.of(orderDto));

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/orders/users/" + orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").exists());
    }

    // get all orders : testing
    @Test
    public void getAllOrdersTest() throws Exception {

        OrderDto orderDto1 = OrderDto.builder().orderId("o456").billingName("Hussain").billingPhone("8508280103").billingAddress("2B, Kazimar Street, Pallivasal Lane, Madurai-625001").orderedDate(new Date()).paymentStatus("NOT_PAID").orderStatus("DISPATCHED").build();
        OrderDto orderDto2 = OrderDto.builder().orderId("0789").billingName("Khader").billingPhone("8508280104").billingAddress("2C, Kazimar Street, Pallivasal Lane, Madurai-625001").orderedDate(new Date()).paymentStatus("PAID").orderStatus("DELIVERED").build();

        PageableResponse<OrderDto> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(Arrays.asList(orderDto, orderDto1, orderDto2));
        pageableResponse.setPageNumber(100);
        pageableResponse.setPageSize(10);
        pageableResponse.setTotalElements(10000);
        pageableResponse.setTotalPages(1000);
        pageableResponse.setLastPage(false);
        Mockito.when(orderService.getOrders(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").exists());
    }

    @Test
    public void updateOrderTest() throws Exception {
        String orderId = "o123";
        Mockito.when(orderService.updateOrder(Mockito.any(), Mockito.anyString())).thenReturn(orderDto);

        UpdateOrderRequestDto updateOrderRequestDto = UpdateOrderRequestDto.builder()
                .billingName("Sabeer")
                .billingPhone("8508280102")
                .billingAddress("2A, Kazimar Street, Pallivasal Lane, Madurai-625001")
                .paymentStatus("PAID")
                .orderStatus("DELIVERED")
                .build();

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/orders/" + orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(updateOrderRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists());
    }

    private String convertObjectToJsonString(Object order) {
        try {
            return new ObjectMapper().writeValueAsString(order);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
