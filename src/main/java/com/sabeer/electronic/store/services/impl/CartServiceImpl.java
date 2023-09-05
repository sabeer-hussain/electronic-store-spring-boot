package com.sabeer.electronic.store.services.impl;

import com.sabeer.electronic.store.dtos.AddItemToCartRequestDto;
import com.sabeer.electronic.store.dtos.CartDto;
import com.sabeer.electronic.store.entities.Cart;
import com.sabeer.electronic.store.entities.CartItem;
import com.sabeer.electronic.store.entities.Product;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.exceptions.BadApiRequestException;
import com.sabeer.electronic.store.exceptions.ResourceNotFoundException;
import com.sabeer.electronic.store.repositories.CartItemRepository;
import com.sabeer.electronic.store.repositories.CartRepository;
import com.sabeer.electronic.store.repositories.ProductRepository;
import com.sabeer.electronic.store.repositories.UserRepository;
import com.sabeer.electronic.store.services.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequestDto request) {
        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if (quantity <= 0) {
            throw new BadApiRequestException("Requested quantity is not valid !!");
        }

        // fetch the product to add the product/item to the cart
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found in database"));

        // fetch the user from db to fetch the cart
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found in database"));

        // approach 1:
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCartId(UUID.randomUUID().toString());
            newCart.setCreatedAt(new Date());
            return newCart;
        });

        // approach 2:
        /*
        Cart cart = null;
        try {
            cart = cartRepository.findByUser(user).get();
        } catch (NoSuchElementException ex) {
            cart = new Cart();
            cart.setCartId(UUID.randomUUID().toString());
            cart.setCreatedAt(new Date());
        }
         */

        // perform cart operation
        // if cart item already present, then update
        AtomicReference<Boolean> updated = new AtomicReference<>(false);
        List<CartItem> items = cart.getItems();
        List<CartItem> updatedItems = items.stream().map(item -> {
            if (item.getProduct().getProductId().equals(productId)) {
                // item already present in cart
                item.setQuantity(quantity);
                item.setTotalPrice(quantity * product.getDiscountedPrice());
                updated.set(true);
                return item;
            }
            return item;
        }).collect(Collectors.toList());

        cart.setItems(updatedItems);

        // create items
        if (!updated.get()) {
            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();

            cart.getItems().add(cartItem);
        }

        cart.setUser(user);

        Cart savedOrUpdatedCart = cartRepository.save(cart);

        return mapper.map(savedOrUpdatedCart, CartDto.class);
    }

    @Override
    public void removeItemFromCart(String userId, int cartItemId) {
        // conditions

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart Item not found !!"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String userId) {
        // fetch the user from db to clear the cart
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found in database"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user not found !!"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDto getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found in database"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user not found !!"));

        return mapper.map(cart, CartDto.class);
    }
}
