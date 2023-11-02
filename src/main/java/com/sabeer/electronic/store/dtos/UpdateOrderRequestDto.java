package com.sabeer.electronic.store.dtos;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderRequestDto {

    private String orderStatus = "PENDING";
    private String paymentStatus = "NOT_PAID";

    @NotBlank(message = "Address is required !!")
    private String billingAddress;

    @NotBlank(message = "Phone number is required !!")
    private String billingPhone;

    @NotBlank(message = "Billing name is required !!")
    private String billingName;

    private Date deliveredDate;
}
