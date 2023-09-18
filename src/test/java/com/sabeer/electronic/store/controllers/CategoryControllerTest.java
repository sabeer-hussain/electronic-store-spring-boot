package com.sabeer.electronic.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabeer.electronic.store.dtos.CategoryDto;
import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.ProductDto;
import com.sabeer.electronic.store.services.CategoryService;
import com.sabeer.electronic.store.services.FileService;
import com.sabeer.electronic.store.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @MockBean
    private FileService fileService;

    private CategoryDto categoryDto;

    private ProductDto productDto;

    @Value("${category.cover.image.path}")
    private String imagePath;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        categoryDto = CategoryDto.builder()
                .title("Cell Phone")
                .description("This is cell phone category")
                .coverImage("category_abc.png")
                .build();
        productDto = ProductDto.builder()
                .title("Redmi Note 5 Pro")
                .description("This is testing product")
                .price(20000)
                .discountedPrice(15000)
                .quantity(3)
                .live(true)
                .stock(true)
                .productImageName("product_abc.png")
                .build();
    }

    @Test
    public void createCategoryTest() throws Exception {
//        /categories + POST + category data as json
//        response: data as json + status created

        Mockito.when(categoryService.create(Mockito.any())).thenReturn(categoryDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/categories")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(categoryDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    public void updateCategoryTest() throws Exception {
//        /categories/{categoryId} + PUT + category data as json
//        response: data as json + status ok

        String categoryId = "123";
        Mockito.when(categoryService.update(Mockito.any(), Mockito.anyString())).thenReturn(categoryDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/categories/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(categoryDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        String categoryId = "123";
        Mockito.doNothing().when(categoryService).delete(Mockito.anyString());

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/categories/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    // get all categories : testing
    @Test
    public void getAllCategoriesTest() throws Exception {
        CategoryDto categoryDto1 = CategoryDto.builder().title("Laptop").description("This is laptop category").coverImage("category_def.png").build();
        CategoryDto categoryDto2 = CategoryDto.builder().title("Electronics").description("This is electronics category").coverImage("category_ghi.png").build();
        CategoryDto categoryDto3 = CategoryDto.builder().title("Headphones").description("This is headphones category").coverImage("category_jkl.png").build();
        CategoryDto categoryDto4 = CategoryDto.builder().title("Mouse").description("This is mouse category").coverImage("category_mno.png").build();

        PageableResponse<CategoryDto> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(Arrays.asList(categoryDto1, categoryDto2, categoryDto3, categoryDto4));
        pageableResponse.setPageNumber(100);
        pageableResponse.setPageSize(10);
        pageableResponse.setTotalElements(10000);
        pageableResponse.setTotalPages(1000);
        pageableResponse.setLastPage(false);
        Mockito.when(categoryService.getAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/categories")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    public void getCategoryTest() throws Exception {
        String categoryId = "123";
        Mockito.when(categoryService.get(Mockito.anyString())).thenReturn(categoryDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/categories/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    public void searchCategoryTest() throws Exception {
        String keywords = "phone";
        List<CategoryDto> categoryDtoList = List.of(categoryDto);
        Mockito.when(categoryService.search(Mockito.anyString())).thenReturn(categoryDtoList);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/categories/search/" + keywords)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    public void uploadCategoryCoverImageTest() throws Exception {
        String categoryId = "123";
        String imageName = "category_abc.png";

        Mockito.when(fileService.uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString())).thenReturn(imageName);
        Mockito.when(categoryService.get(Mockito.anyString())).thenReturn(categoryDto);
        Mockito.when(categoryService.update(Mockito.any(), Mockito.anyString())).thenReturn(categoryDto);

        String name = "coverImage";
        String originalFileName = "category_abc.png";
        String contentType = "image/jpeg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(imagePath + name));
        } catch (final IOException e) {
        }

        MockMultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);


        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/categories/image/" + categoryId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void serveCoverImageTest() throws Exception {
        String categoryId = "123";
        Mockito.when(categoryService.get(Mockito.anyString())).thenReturn(categoryDto);
        FileInputStream inputStream = new FileInputStream(imagePath + "category_abc.png");
        Mockito.when(fileService.getResource(Mockito.anyString(), Mockito.anyString())).thenReturn(inputStream);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/categories/image/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void createProductWithCategoryTest() throws Exception {
        String categoryId = "123";

        Mockito.when(productService.createWithCategory(Mockito.any(ProductDto.class), Mockito.anyString())).thenReturn(productDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/categories/" + categoryId + "/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(categoryDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    public void updateCategoryOfProductTest() throws Exception {
        String categoryId = "c123";
        String productId = "p123";

        Mockito.when(productService.updateCategory(Mockito.anyString(), Mockito.anyString())).thenReturn(productDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/categories/" + categoryId + "/products/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(categoryDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    public void getProductsOfCategoryTest() throws Exception {
        String categoryId = "123";

        ProductDto productDto1 = ProductDto.builder().title("Apple iphone").description("This is testing product").price(70000).discountedPrice(65000).quantity(2).live(true).stock(false).productImageName("product_def.png").build();
        ProductDto productDto2 = ProductDto.builder().title("Samsung S22 Ultra").description("This is testing product").price(60000).discountedPrice(55000).quantity(2).live(false).stock(true).productImageName("product_ghi.png").build();
        ProductDto productDto3 = ProductDto.builder().title("Dell Laptop").description("This is testing product").price(150000).discountedPrice(145000).quantity(2).live(true).stock(true).productImageName("product_jkl.png").build();
        ProductDto productDto4 = ProductDto.builder().title("Zebronics Mouse").description("This is testing product").price(250).discountedPrice(200).quantity(2).live(true).stock(true).productImageName("product_mno.png").build();

        PageableResponse<ProductDto> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(Arrays.asList(productDto1, productDto2, productDto3, productDto4));
        pageableResponse.setPageNumber(100);
        pageableResponse.setPageSize(10);
        pageableResponse.setTotalElements(10000);
        pageableResponse.setTotalPages(1000);
        pageableResponse.setLastPage(false);
        Mockito.when(productService.getAllOfCategory(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/categories/" + categoryId + "/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    private String convertObjectToJsonString(Object category) {
        try {
            return new ObjectMapper().writeValueAsString(category);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
