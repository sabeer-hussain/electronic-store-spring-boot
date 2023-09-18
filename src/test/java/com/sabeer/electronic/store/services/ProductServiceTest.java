package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.ProductDto;
import com.sabeer.electronic.store.entities.Category;
import com.sabeer.electronic.store.entities.Product;
import com.sabeer.electronic.store.exceptions.ResourceNotFoundException;
import com.sabeer.electronic.store.repositories.CategoryRepository;
import com.sabeer.electronic.store.repositories.ProductRepository;
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
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ProductServiceTest {

    // TODO:2 complete test cases for Product Service

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper mapper;

    private Product product;

    private Category category;

    @Value("${product.image.path}")
    private String imagePath;

    @BeforeEach
    public void init() {
        category = Category.builder()
                .title("Mobile Phone")
                .description("This is mobile phone category")
                .coverImage("category_abc.png")
                .build();
        product = Product.builder()
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

    // create product
    @Test
    public void createProductTest() {
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(product);

        ProductDto createdProduct = productService.create(mapper.map(product, ProductDto.class));

        Assertions.assertNotNull(createdProduct);
        Assertions.assertEquals(product.getTitle(), createdProduct.getTitle());
    }

    // update product test
    @Test
    public void updateProductTest() {
        String productId = "123";

        ProductDto productDto = ProductDto.builder()
                .title("Redmi Note 5 Pro")
                .description("This is testing product")
                .price(15000)
                .discountedPrice(12000)
                .quantity(2)
                .live(true)
                .stock(true)
                .productImageName("product_abc.png")
                .build();

        Mockito.when(productRepository.findById(Mockito.anyString())).thenReturn(Optional.of(product));
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(product);

        ProductDto updatedProduct = productService.update(productDto, productId);

        Assertions.assertNotNull(updatedProduct);
        Assertions.assertEquals(productDto.getTitle(), updatedProduct.getTitle(), "Name is not validated !!");
        Assertions.assertEquals(productDto.getProductImageName(), updatedProduct.getProductImageName());
        // multiple assertions are valid..
    }

    @Test
    public void updateProduct_ResourceNotFoundException_Test() {
        String productId = "123";

//        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.update(mapper.map(product, ProductDto.class), productId));
    }

    // delete product test case
    @Test
    public void deleteProductTest() throws IOException {
        String productId = "productIdabc";

        Mockito.when(productRepository.findById("productIdabc")).thenReturn(Optional.of(product));
//        Mockito.doNothing().when(productRepository).delete(Mockito.any());

        File folder = new File(imagePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        FileSystem fileSys = FileSystems.getDefault();
        Path originalFilePath = fileSys.getPath(imagePath +"/product_abc.png");
        Path tempFilePath = fileSys.getPath(imagePath + "/product_temp.png");
        Files.copy(originalFilePath, tempFilePath);

        productService.delete(productId);

        Mockito.verify(productRepository, Mockito.times(1)).delete(product);

        Files.copy(tempFilePath, originalFilePath);
        Files.delete(tempFilePath);
    }

    @Test
    public void deleteProduct_ResourceNotFoundException_Test() {
        String productId = "productIdabc";

//        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.delete(productId));
    }

    @Test
    public void deleteProduct_NoSuchFileException_Test() {
        String productId = "productIdabc";

        product.setProductImageName("xyz.png");
        Mockito.when(productRepository.findById("productIdabc")).thenReturn(Optional.of(product));
//        Mockito.doNothing().when(productRepository).delete(Mockito.any());

        productService.delete(productId);

        Mockito.verify(productRepository, Mockito.times(1)).delete(product);
    }

    // get product by id test case
    @Test
    public void getProductByIdTest() {
        String productId = "productIdTest";

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // actual call of service method
        ProductDto productDto = productService.get(productId);

        Assertions.assertNotNull(productDto);
        Assertions.assertEquals(product.getTitle(), productDto.getTitle(), "Name not matched !!");
    }

    @Test
    public void getProductById_ResourceNotFoundException_Test() {
//        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.get("123"));
    }

    // get all products
    @Test
    public void getAllProductsTest() {
        Product product1 = Product.builder()
                .title("Apple iphone")
                .description("This is testing product")
                .price(70000)
                .discountedPrice(65000)
                .quantity(2)
                .live(true)
                .stock(false)
                .productImageName("product_def.png")
                .build();

        Product product2 = Product.builder()
                .title("Samsung S22 Ultra")
                .description("This is testing product")
                .price(60000)
                .discountedPrice(55000)
                .quantity(2)
                .live(false)
                .stock(true)
                .productImageName("product_def.png")
                .build();

        List<Product> productList = List.of(product, product1, product2);
        Page<Product> page = new PageImpl<>(productList);

        Mockito.when(productRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<ProductDto> allProducts = productService.getAll(1, 2, "title", "asc");

        Assertions.assertEquals(3, allProducts.getContent().size());
    }

    @Test
    public void getAllProducts_SortByByTitleInDescending_Test() {
        Product product1 = Product.builder()
                .title("Apple iphone")
                .description("This is testing product")
                .price(70000)
                .discountedPrice(65000)
                .quantity(2)
                .live(true)
                .stock(false)
                .productImageName("product_def.png")
                .build();

        Product product2 = Product.builder()
                .title("Samsung S22 Ultra")
                .description("This is testing product")
                .price(60000)
                .discountedPrice(55000)
                .quantity(2)
                .live(false)
                .stock(true)
                .productImageName("product_ghi.png")
                .build();

        List<Product> productList = List.of(product, product1, product2);
        Page<Product> page = new PageImpl<>(productList);

        Mockito.when(productRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<ProductDto> allProducts = productService.getAll(1, 2, "name", "desc");

        Assertions.assertEquals(3, allProducts.getContent().size());
    }

    // get all live products
    @Test
    public void getAllLiveProductsTest() {
        Product product1 = Product.builder()
                .title("Apple iphone")
                .description("This is testing product")
                .price(70000)
                .discountedPrice(65000)
                .quantity(2)
                .live(true)
                .stock(false)
                .productImageName("product_def.png")
                .build();

        Product product2 = Product.builder()
                .title("Samsung S22 Ultra")
                .description("This is testing product")
                .price(60000)
                .discountedPrice(55000)
                .quantity(2)
                .live(false)
                .stock(true)
                .productImageName("product_def.png")
                .build();

        List<Product> productList = List.of(product, product1, product2);
        Page<Product> page = new PageImpl<>(productList);

        Mockito.when(productRepository.findByLiveTrue(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<ProductDto> allProducts = productService.getAllLive(1, 2, "title", "asc");

        Assertions.assertEquals(3, allProducts.getContent().size());
    }

    @Test
    public void getAllLiveProducts_SortByByTitleInDescending_Test() {
        Product product1 = Product.builder()
                .title("Apple iphone")
                .description("This is testing product")
                .price(70000)
                .discountedPrice(65000)
                .quantity(2)
                .live(true)
                .stock(false)
                .productImageName("product_def.png")
                .build();

        Product product2 = Product.builder()
                .title("Samsung S22 Ultra")
                .description("This is testing product")
                .price(60000)
                .discountedPrice(55000)
                .quantity(2)
                .live(false)
                .stock(true)
                .productImageName("product_ghi.png")
                .build();

        List<Product> productList = List.of(product, product1, product2);
        Page<Product> page = new PageImpl<>(productList);

        Mockito.when(productRepository.findByLiveTrue(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<ProductDto> allProducts = productService.getAllLive(1, 2, "name", "desc");

        Assertions.assertEquals(3, allProducts.getContent().size());
    }

    // search product by title test case
    @Test
    public void searchByTitleTest() {
        Product product1 = Product.builder()
                .title("Apple iphone")
                .description("This is testing product")
                .price(70000)
                .discountedPrice(65000)
                .quantity(2)
                .live(true)
                .stock(false)
                .productImageName("product_def.png")
                .build();

        Product product2 = Product.builder()
                .title("Samsung S22 Ultra")
                .description("This is testing product")
                .price(60000)
                .discountedPrice(55000)
                .quantity(2)
                .live(false)
                .stock(true)
                .productImageName("product_ghi.png")
                .build();

        Product product3 = Product.builder()
                .title("Dell Laptop")
                .description("This is testing product")
                .price(150000)
                .discountedPrice(145000)
                .quantity(2)
                .live(true)
                .stock(true)
                .productImageName("product_jkl.png")
                .build();

        List<Product> productList = List.of(product, product1, product2, product3);
        Page<Product> page = new PageImpl<>(productList);

        String keywords = "phone";
        Mockito.when(productRepository.findByTitleContaining(Mockito.eq(keywords), Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<ProductDto> pageableProductDtoResponse = productService.searchByTitle(keywords, 1,2, "title", "asc");

        Assertions.assertEquals(4, pageableProductDtoResponse.getContent().size(), "Size not matched !!");
    }

    // create product with category
    @Test
    public void createWithCategoryTest() {
        String categoryId = "123";
        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(category));
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(product);

        ProductDto createdProductWithCategory = productService.createWithCategory(mapper.map(product, ProductDto.class), categoryId);

        Assertions.assertNotNull(createdProductWithCategory);
        Assertions.assertEquals(product.getTitle(), createdProductWithCategory.getTitle());
    }

    @Test
    public void createWithCategory_CategoryResourceNotFoundException_Test() {
        String categoryId = "123";

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.createWithCategory(mapper.map(product, ProductDto.class), categoryId));
    }

    // update category for product
    @Test
    public void updateCategoryTest() {
        String productId = "p123";
        String categoryId = "c123";
        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.of(product));
        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(category));
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(product);

        ProductDto updatedProductWithCategory = productService.updateCategory(productId, categoryId);

        Assertions.assertNotNull(updatedProductWithCategory);
        Assertions.assertEquals(product.getTitle(), updatedProductWithCategory.getTitle());
    }

    @Test
    public void updateCategory_ProductResourceNotFoundException_Test() {
        String productId = "p123";
        String categoryId = "c123";

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.updateCategory(productId, categoryId));
    }

    @Test
    public void updateCategory_CategoryResourceNotFoundException_Test() {
        String productId = "p123";
        String categoryId = "c123";
        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.of(product));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.updateCategory(productId, categoryId));
    }

    // get all products of a category
    @Test
    public void getAllOfCategoryTest() {
        String categoryId = "c123";

        Product product1 = Product.builder()
                .title("Apple iphone")
                .description("This is testing product")
                .price(70000)
                .discountedPrice(65000)
                .quantity(2)
                .live(true)
                .stock(false)
                .productImageName("product_def.png")
                .build();

        Product product2 = Product.builder()
                .title("Samsung S22 Ultra")
                .description("This is testing product")
                .price(60000)
                .discountedPrice(55000)
                .quantity(2)
                .live(false)
                .stock(true)
                .productImageName("product_def.png")
                .build();

        List<Product> productList = List.of(product, product1, product2);
        Page<Product> page = new PageImpl<>(productList);

        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(category));
        Mockito.when(productRepository.findByCategory(Mockito.any(), Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<ProductDto> allProducts = productService.getAllOfCategory(categoryId, 1, 2, "title", "asc");

        Assertions.assertEquals(3, allProducts.getContent().size());
    }

    @Test
    public void getAllOfCategory_SortByByTitleInDescending_Test() {
        String categoryId = "c123";

        Product product1 = Product.builder()
                .title("Apple iphone")
                .description("This is testing product")
                .price(70000)
                .discountedPrice(65000)
                .quantity(2)
                .live(true)
                .stock(false)
                .productImageName("product_def.png")
                .build();

        Product product2 = Product.builder()
                .title("Samsung S22 Ultra")
                .description("This is testing product")
                .price(60000)
                .discountedPrice(55000)
                .quantity(2)
                .live(false)
                .stock(true)
                .productImageName("product_def.png")
                .build();

        List<Product> productList = List.of(product, product1, product2);
        Page<Product> page = new PageImpl<>(productList);

        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(category));
        Mockito.when(productRepository.findByCategory(Mockito.any(), Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<ProductDto> allProducts = productService.getAllOfCategory(categoryId, 1, 2, "title", "desc");

        Assertions.assertEquals(3, allProducts.getContent().size());
    }

    @Test
    public void getAllOfCategory_ResourceNotFoundException_Test() {
        String categoryId = "c123";

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.getAllOfCategory(categoryId, 1, 2, "title", "desc"));
    }
}
