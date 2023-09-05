package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.AddItemToCartRequestDto;
import com.sabeer.electronic.store.dtos.CartDto;

public interface CartService {

    // add items to cart
        // case 1: If cart for user is not available, then we will create the cart and then add the items.
        // case 2: If cart for user is available, then we will add the items to cart.
    CartDto addItemToCart(String userId, AddItemToCartRequestDto request);

    // remove item from cart
    void removeItemFromCart(String userId, int cartItemId);

    // remove all items from cart
    void clearCart(String userId);
}
