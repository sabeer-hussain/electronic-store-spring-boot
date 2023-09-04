package com.sabeer.electronic.store.config;

import com.sabeer.electronic.store.dtos.ProductDto;
import com.sabeer.electronic.store.entities.Product;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Product.class, ProductDto.class)
                .addMapping(Product::getCategory, ProductDto::setCategoryDto);
        return modelMapper;
    }
}
