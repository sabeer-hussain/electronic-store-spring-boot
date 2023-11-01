package com.sabeer.electronic.store.controllers;

import com.sabeer.electronic.store.dtos.*;
import com.sabeer.electronic.store.services.FileService;
import com.sabeer.electronic.store.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/products")
//@CrossOrigin(origins = "*")
@Tag(name = "ProductController", description = "REST APIs related to perform product operations !!")
public class ProductController {

    @Value("${product.image.path}")
    private String imagePath;

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    // create
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        ProductDto createdProduct = productService.create(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String productId, @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.update(productDto, productId);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    // delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId) {
        productService.delete(productId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .message("Product is deleted successfully !!")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // get single
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String productId) {
        ProductDto productDto = productService.get(productId);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    // get all
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageableResponse<ProductDto> pageableResponse = productService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    // get all live
    //      /products/live
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLiveProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageableResponse<ProductDto> pageableResponse = productService.getAllLive(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    // search product
    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProduct(
            @PathVariable String query,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        PageableResponse<ProductDto> pageableResponse = productService.searchByTitle(query, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    // upload product image
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam("productImage") MultipartFile image) throws IOException {
        ProductDto productDto = productService.get(productId);
        String fileName = fileService.uploadFile(image, imagePath);
        productDto.setProductImageName(fileName);
        ProductDto updatedProduct = productService.update(productDto, productId);

        ImageResponse response = ImageResponse.builder()
                                        .imageName(updatedProduct.getProductImageName())
                                        .message("Product image is successfully uploaded !!")
                                        .status(HttpStatus.CREATED)
                                        .success(true)
                                    .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // update product image
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> updateProductImage(
            @PathVariable String productId,
            @RequestParam("productImage") MultipartFile image) throws IOException {
        ProductDto productDto = productService.get(productId);
        String productImageName = productDto.getProductImageName();
        String fileName = fileService.uploadFile(image, imagePath);
        productDto.setProductImageName(fileName);
        ProductDto updatedProduct = productService.update(productDto, productId);

        // delete existing product image for the productId after updated new product image
        if (productImageName != null) {
            fileService.deleteFile(imagePath, productImageName);
        }

        ImageResponse response = ImageResponse.builder()
                .imageName(updatedProduct.getProductImageName())
                .message("Product image is successfully updated !!")
                .status(HttpStatus.OK)
                .success(true)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // serve product image
    @GetMapping("/image/{productId}")
    public void serveProductImage(@PathVariable String productId, HttpServletResponse response) throws IOException {
        ProductDto productDto = productService.get(productId);
        InputStream resource = fileService.getResource(imagePath, productDto.getProductImageName());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }
}
