package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.CategoryDto;
import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.entities.Category;
import com.sabeer.electronic.store.exceptions.ResourceNotFoundException;
import com.sabeer.electronic.store.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class CategoryServiceTest {

    // TODO:1 complete test cases for Category Service

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper mapper;

    private Category category;

    @Value("${category.cover.image.path}")
    private String imagePath;

    @BeforeEach
    public void init() {
        category = Category.builder()
                .title("Cell Phone")
                .description("This is cell phone category")
                .coverImage("category_abc.png")
                .build();
    }

    // create category
    @Test
    public void createCategoryTest() {
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(category);

        CategoryDto createdCategory = categoryService.create(mapper.map(category, CategoryDto.class));

        Assertions.assertNotNull(createdCategory);
        Assertions.assertEquals(category.getTitle(), createdCategory.getTitle());
    }

    // update category test
    @Test
    public void updateCategoryTest() {
        String categoryId = "123";

        CategoryDto categoryDto = CategoryDto.builder()
                .title("Mobile Phone")
                .description("This is mobile phone category")
                .coverImage("category_abc.png")
                .build();

        Mockito.when(categoryRepository.findById(Mockito.anyString())).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(category);

        CategoryDto updatedCategory = categoryService.update(categoryDto, categoryId);

        Assertions.assertNotNull(updatedCategory);
        Assertions.assertEquals(category.getTitle(), updatedCategory.getTitle(), "Name is not validated !!");
        Assertions.assertEquals(category.getCoverImage(), updatedCategory.getCoverImage());
        // multiple assertions are valid..
    }

    @Test
    public void updateCategory_ResourceNotFoundException_Test() {
        String categoryId = "123";

//        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.update(mapper.map(category, CategoryDto.class), categoryId));
    }

    // delete category test case
    @Test
    public void deleteCategoryTest() throws IOException {
        String categoryId = "categoryIdabc";

        Mockito.when(categoryRepository.findById("categoryIdabc")).thenReturn(Optional.of(category));
//        Mockito.doNothing().when(categoryRepository).delete(Mockito.any());

        File folder = new File(imagePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        FileSystem fileSys = FileSystems.getDefault();
        Path originalFilePath = fileSys.getPath(imagePath +"/category_abc.png");
        Path tempFilePath = fileSys.getPath(imagePath + "/category_temp.png");
        Files.copy(originalFilePath, tempFilePath);

        categoryService.delete(categoryId);

        Mockito.verify(categoryRepository, Mockito.times(1)).delete(category);

        Files.copy(tempFilePath, originalFilePath);
        Files.delete(tempFilePath);
    }

    @Test
    public void deleteCategory_ResourceNotFoundException_Test() {
        String categoryId = "categoryIdabc";

//        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(categoryId));
    }

    @Test
    public void deleteCategory_NoSuchFileException_Test() {
        String categoryId = "categoryIdabc";

        category.setCoverImage("category_xyz.png");
        Mockito.when(categoryRepository.findById("categoryIdabc")).thenReturn(Optional.of(category));
//        Mockito.doNothing().when(categoryRepository).delete(Mockito.any());

        categoryService.delete(categoryId);

        Mockito.verify(categoryRepository, Mockito.times(1)).delete(category);
    }

    // get all categories
    @Test
    public void getAllCategoriesTest() {
        Category category1 = Category.builder()
                .title("Laptop")
                .description("This is laptop category")
                .coverImage("category_def.png")
                .build();

        Category category2 = Category.builder()
                .title("Electronics")
                .description("This is electronics category")
                .coverImage("category_ghi.png")
                .build();

        List<Category> categoryList = List.of(category, category1, category2);
        Page<Category> page = new PageImpl<>(categoryList);

        Mockito.when(categoryRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<CategoryDto> allCategories = categoryService.getAll(1, 2, "title", "asc");

        Assertions.assertEquals(3, allCategories.getContent().size());
    }

    @Test
    public void getAllCategories_SortByByTitleInDescending_Test() {
        Category category1 = Category.builder()
                .title("Laptop")
                .description("This is laptop category")
                .coverImage("category_def.png")
                .build();

        Category category2 = Category.builder()
                .title("Electronics")
                .description("This is electronics category")
                .coverImage("category_ghi.png")
                .build();

        List<Category> categoryList = List.of(category, category1, category2);
        Page<Category> page = new PageImpl<>(categoryList);

        Mockito.when(categoryRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<CategoryDto> allCategories = categoryService.getAll(1, 2, "title", "desc");

        Assertions.assertEquals(3, allCategories.getContent().size());
    }

    // get category by id test case
    @Test
    public void getCategoryByIdTest() {
        String categoryId = "categoryIdTest";

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // actual call of service method
        CategoryDto categoryDto = categoryService.get(categoryId);

        Assertions.assertNotNull(categoryDto);
        Assertions.assertEquals(category.getTitle(), categoryDto.getTitle(), "Title not matched !!");
    }

    @Test
    public void getCategoryById_ResourceNotFoundException_Test() {
//        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.get("123"));
    }

    // search category test case
    @Test
    public void searchCategoryTest() {
        Category category1 = Category.builder()
                .title("Laptop")
                .description("This is laptop category")
                .coverImage("category_def.png")
                .build();

        Category category2 = Category.builder()
                .title("Electronics")
                .description("This is electronics category")
                .coverImage("category_ghi.png")
                .build();

        Category category3 = Category.builder()
                .title("Headphones")
                .description("This is headphones category")
                .coverImage("category_jkl.png")
                .build();

        String keywords = "phone";
        Mockito.when(categoryRepository.findByTitleContaining(keywords)).thenReturn(Arrays.asList(category, category1, category2, category3));

        List<CategoryDto> categoryDtos = categoryService.search(keywords);

        Assertions.assertEquals(4, categoryDtos.size(), "Size not matched !!");
    }
}
