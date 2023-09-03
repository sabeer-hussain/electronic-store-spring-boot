package com.sabeer.electronic.store.services.impl;

import com.sabeer.electronic.store.dtos.CategoryDto;
import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.entities.Category;
import com.sabeer.electronic.store.exceptions.ResourceNotFoundException;
import com.sabeer.electronic.store.helper.Helper;
import com.sabeer.electronic.store.repositories.CategoryRepository;
import com.sabeer.electronic.store.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Value("${category.cover.image.path}")
    private String imagePath;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        // creating categoryId: randomly
        String categoryId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);
        Category category = mapper.map(categoryDto, Category.class);

        Category savedCategory = categoryRepository.save(category);

        return mapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {
        // get category of given id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id !!"));

        // update category details
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());
        Category updatedCategory = categoryRepository.save(category);

        return mapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public void delete(String categoryId) {
        // get category of given id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(("Category not found with given id !!")));

        // delete category cover image
        // images/categories/abc.png
        String fullPath = imagePath + category.getCoverImage();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException ex) {
            LOGGER.error("Category cover image not found in folder");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // delete category
        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);

        PageableResponse<CategoryDto> pageableResponse = Helper.getPageableResponse(page, CategoryDto.class);

        return pageableResponse;
    }

    @Override
    public CategoryDto get(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(("Category not found with given id !!")));
        return mapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> search(String keyword) {
        List<Category> categories = categoryRepository.findByTitleContaining(keyword);
        List<CategoryDto> dtoList = categories.stream().map(category -> mapper.map(category, CategoryDto.class)).collect(Collectors.toList());
        return dtoList;
    }
}
