package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.CategoryDto;
import com.sabeer.electronic.store.dtos.PageableResponse;

import java.util.List;

public interface CategoryService {

    // create category
    CategoryDto create(CategoryDto categoryDto);

    // update category
    CategoryDto update(CategoryDto categoryDto, String categoryId);

    // delete category
    void delete(String categoryId);

    // get all categories
    PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    // get single category detail
    CategoryDto get(String categoryId);

    // search category
    List<CategoryDto> search(String keyword);

    // other category specific features
}
