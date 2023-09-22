package com.sabeer.electronic.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabeer.electronic.store.dtos.*;
import com.sabeer.electronic.store.entities.Role;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.security.JwtHelper;
import com.sabeer.electronic.store.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtHelper jwtHelper;

    private UserDto userDto;

    private RoleDto roleDto;

    private ProductDto productDto;

    private CartDto cartDto;

    private CartItemDto cartItemDto;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        roleDto = RoleDto.builder()
                .roleName("abc")
                .roleName("NORMAL")
                .build();
        userDto = UserDto.builder()
                .name("Sabeer")
                .email("sabeer@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .roles(Set.of(roleDto))
                .build();
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
        cartItemDto = CartItemDto.builder()
                .cartItemId(123)
                .totalPrice(45000)
                .quantity(3)
                .product(productDto)
                .build();
        cartDto = CartDto.builder()
                .cartId("c123")
                .createdAt(new Date())
                .user(userDto)
                .items(new ArrayList<>(List.of(cartItemDto)))
                .build();
    }

    @Test
    public void addItemToCartTest() throws Exception {
        mockSecurity();

        String userId = "123";

        Mockito.when(cartService.addItemToCart(Mockito.anyString(), Mockito.any(AddItemToCartRequestDto.class))).thenReturn(cartDto);

        AddItemToCartRequestDto addItemToCartRequestDto = AddItemToCartRequestDto.builder()
                .quantity(3)
                .productId("p123")
                .build();

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/carts/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(addItemToCartRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").exists());
    }

    @Test
    public void removeItemFromCartTest() throws Exception {
        mockSecurity();

        String userId = "u123";
        int itemId = 123;

        Mockito.doNothing().when(cartService).removeItemFromCart(Mockito.anyString(), Mockito.anyInt());

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/carts/" + userId + "/items/" + itemId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void clearCartTest() throws Exception {
        mockSecurity();

        String userId = "u123";

        Mockito.doNothing().when(cartService).clearCart(Mockito.anyString());

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/carts/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void getCartTest() throws Exception {
        mockSecurity();

        String userId = "u123";

        Mockito.when(cartService.getCartByUser(Mockito.anyString())).thenReturn(cartDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/carts/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").exists());
    }

    private void mockSecurity() {
        Mockito.when(jwtHelper.getUsernameFromToken(Mockito.any())).thenReturn("msabeerhussain007@gmail.com");

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        Role role = Role.builder()
                .roleName("wetrsdfwetwfasfwdf")
                .roleName("ROLE_ADMIN")
                .build();
        User user = User.builder()
                .userId("05a268c9-e80b-45a7-8ad1-eaefc17a08ce")
                .name("Sabeer Hussain")
                .email("msabeerhussain007@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("I am java developer")
                .imageName("0b70a62d-3f32-4599-95a4-3d932c7605fd.jpg")
                .roles(Set.of(role))
                .build();
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);

        Mockito.when(jwtHelper.validateToken(Mockito.anyString(), Mockito.any())).thenReturn(true);
    }

    private String convertObjectToJsonString(Object cart) {
        try {
            return new ObjectMapper().writeValueAsString(cart);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
