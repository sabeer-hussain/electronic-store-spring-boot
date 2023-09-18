package com.sabeer.electronic.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.ProductDto;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private FileService fileService;

    private ProductDto productDto;

    @Value("${product.image.path}")
    private String imagePath;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
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
    public void createProductTest() throws Exception {
//        /products + POST + product data as json
//        response: data as json + status created

        Mockito.when(productService.create(Mockito.any())).thenReturn(productDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(productDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    public void updateProductTest() throws Exception {
//        /products/{productId} + PUT + products data as json
//        response: data as json + status ok

        String productId = "123";
        Mockito.when(productService.update(Mockito.any(), Mockito.anyString())).thenReturn(productDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/products/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(productDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    public void deleteProductTest() throws Exception {
        String productId = "123";
        Mockito.doNothing().when(productService).delete(Mockito.anyString());

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/products/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    // get all products : testing
    @Test
    public void getAllProductsTest() throws Exception {
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
        Mockito.when(productService.getAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    public void getProductTest() throws Exception {
        String productId = "123";
        Mockito.when(productService.get(Mockito.anyString())).thenReturn(productDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/products/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists());
    }

    // get all live products : testing
    @Test
    public void getAllLiveProductsTest() throws Exception {
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
        Mockito.when(productService.getAllLive(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/products/live")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    public void searchProductTest() throws Exception {
        String keywords = "phone";

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

        Mockito.when(productService.searchByTitle(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/products/search/" + keywords)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    public void uploadProductImageTest() throws Exception {
        String productId = "123";
        String imageName = "product_abc.png";

        Mockito.when(productService.get(Mockito.anyString())).thenReturn(productDto);
        Mockito.when(fileService.uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString())).thenReturn(imageName);
        Mockito.when(productService.update(Mockito.any(), Mockito.anyString())).thenReturn(productDto);

        String name = "productImage";
        String originalFileName = "product_abc.png";
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
                        .multipart("/products/image/" + productId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void serveProductImageTest() throws Exception {
        String productId = "123";
        Mockito.when(productService.get(Mockito.anyString())).thenReturn(productDto);
        FileInputStream inputStream = new FileInputStream(imagePath + "product_abc.png");
        Mockito.when(fileService.getResource(Mockito.anyString(), Mockito.anyString())).thenReturn(inputStream);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/products/image/" + productId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTUwMTQzMzIsImV4cCI6MTY5NTAzMjMzMn0.LdfILm4J_FI1ZS06pegfemU9iy-8nRWv53OrL1VElWBeeCULJq_0zhncI_XAhK2b5vdmtjQb817dAd_ZPRFeMw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private String convertObjectToJsonString(Object product) {
        try {
            return new ObjectMapper().writeValueAsString(product);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
