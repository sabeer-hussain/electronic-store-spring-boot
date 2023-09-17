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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.*;

@SpringBootTest
public class CartServiceTest {

    // TODO:3 complete test cases for Cart Service

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper mapper;

    private User user;

    private Role role;

    private Product product;

    private Cart cart;

    private CartItem cartItem;

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
    }

    // add item to cart test
    @Test
    public void addItemToCartTest() {
        String userId = "u123";
        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.of(product));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(cartRepository.findByUser(Mockito.any())).thenReturn(Optional.of(cart));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cart);

        AddItemToCartRequestDto addItemToCartRequestDto = AddItemToCartRequestDto.builder()
                .quantity(3)
                .productId("p123")
                .build();

        CartDto createdCart = cartService.addItemToCart(userId, addItemToCartRequestDto);

        Assertions.assertNotNull(createdCart);
        Assertions.assertEquals(cart.getItems().size(), createdCart.getItems().size());
    }

    @Test
    public void addItemToCart_BadApiRequestException_Test() {
        String userId = "u123";

        AddItemToCartRequestDto addItemToCartRequestDto = AddItemToCartRequestDto.builder()
                .quantity(0)
                .productId("p123")
                .build();

        Assertions.assertThrows(BadApiRequestException.class, () -> cartService.addItemToCart(userId, addItemToCartRequestDto));
    }

    @Test
    public void addItemToCart_NoCartItem_Test() {
        String userId = "u123";
        Cart cart1 = Cart.builder()
                .createdAt(new Date())
                .user(user)
                .build();

        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.of(product));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(cartRepository.findByUser(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cart);

        AddItemToCartRequestDto addItemToCartRequestDto = AddItemToCartRequestDto.builder()
                .quantity(3)
                .productId("p123")
                .build();

        CartDto createdCart = cartService.addItemToCart(userId, addItemToCartRequestDto);

        Assertions.assertNotNull(createdCart);
        Assertions.assertEquals(cart.getItems().size(), createdCart.getItems().size());
    }

    // remove item from cart test
    @Test
    public void removeItemFromCartTest() {
        String userId = "u123";
        int cartItemId = 123;

        Mockito.when(cartItemRepository.findById(Mockito.any())).thenReturn(Optional.of(cartItem));

        cartService.removeItemFromCart(userId, cartItemId);

        Mockito.verify(cartItemRepository, Mockito.times(1)).delete(Mockito.any());
    }

    @Test
    public void removeItemFromCart_ResourceNotFoundException_Test() {
        String userId = "u123";
        int cartItemId = 123;

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartService.removeItemFromCart(userId, cartItemId));
    }

    // clear cart test
    @Test
    public void clearCartTest() {
        String userId = "u123";

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(cartRepository.findByUser(Mockito.any())).thenReturn(Optional.of(cart));

        cartService.clearCart(userId);

        Mockito.verify(cartRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void clearCart_UserResourceNotFoundException_Test() {
        String userId = "u123";

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartService.clearCart(userId));
    }

    @Test
    public void clearCart_CartResourceNotFoundException_Test() {
        String userId = "u123";

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartService.clearCart(userId));
    }

    // get cart by user test
    @Test
    public void getCartByUserTest() {
        String userId = "u123";

        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(cartRepository.findByUser(Mockito.any())).thenReturn(Optional.of(cart));

        CartDto cartDto = cartService.getCartByUser(userId);

        Assertions.assertNotNull(cartDto);
        Assertions.assertEquals(user.getName(), cart.getUser().getName());
    }

    @Test
    public void getCartByUser_UserResourceNotFoundException_Test() {
        String userId = "u123";

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartService.getCartByUser(userId));
    }

    @Test
    public void getCartByUser_CartResourceNotFoundException_Test() {
        String userId = "u123";

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartService.getCartByUser(userId));
    }

}
