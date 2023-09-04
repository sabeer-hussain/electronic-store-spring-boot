package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.ProductDto;

public interface ProductService {

    // create product
    ProductDto create(ProductDto productDto);

    // update product
    ProductDto update(ProductDto productDto, String productId);

    // delete product
    void delete(String productId);

    // get single product (by id)
    ProductDto get(String productId);

    // get all products
    PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    // get all products those are live
    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir);

    // search products by title
    PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir);

    // create product with category
    ProductDto createWithCategory(ProductDto productDto, String categoryId);

    // update category of product
    ProductDto updateCategory(String productId, String categoryId);

    // other methods
}
