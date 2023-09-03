package com.sabeer.electronic.store.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private String categoryId;

    @NotBlank(message = "Title is required !!")
    @Size(min = 4, message = "Title must be of minimum 4 characters !!")
    private String title;

    @NotBlank(message = "Description required !!")
    private String description;

    private String coverImage;
}
