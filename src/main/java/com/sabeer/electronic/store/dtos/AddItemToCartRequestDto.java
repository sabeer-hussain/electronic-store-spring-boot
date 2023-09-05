package com.sabeer.electronic.store.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddItemToCartRequestDto {

    private String productId;

    private int quantity;
}
