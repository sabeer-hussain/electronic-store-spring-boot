package com.sabeer.electronic.store.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequestDto {

    @NotBlank(message = "Cart id is required !!")
    private String cartId;

    @NotBlank(message = "user id is required !!")
    private String userId;

    private String orderStatus = "PENDING";
    private String paymentStatus = "NOT_PAID";

    @NotBlank(message = "Address is required !!")
    private String billingAddress;

    @NotBlank(message = "Phone number is required !!")
    private String billingPhone;

    @NotBlank(message = "Billing name is required !!")
    private String billingName;
}
