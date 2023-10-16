package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.exceptions.BadApiRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String userImagePath;

    @Value("${product.image.path}")
    private String productImagePath;

    @Test
    public void uploadFile_Test() throws IOException {
        String name = "user_abc.png";
        String originalFileName = "user_abc.png";
        String contentType = "image/jpeg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(userImagePath + name));
        } catch (final IOException e) {
        }

        MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

        String uploadedFileName = fileService.uploadFile(multipartFile, userImagePath);

        Assertions.assertTrue(uploadedFileName.endsWith(".png"));
    }

    @Test
    public void uploadFile_BadApiRequestException_Test() {
        String name = "user_abc.txt";
        String originalFileName = "user_abc.txt";
        String contentType = "image/jpeg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(userImagePath + name));
        } catch (final IOException e) {
        }

        MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

        Assertions.assertThrows(BadApiRequestException.class, () -> fileService.uploadFile(multipartFile, userImagePath));
    }

    @Test
    public void deleteFile_Test() throws IOException {
        String name = "product_abc.png";

        FileSystem fileSys = FileSystems.getDefault();
        Path originalFilePath = fileSys.getPath(productImagePath + "/product_abc.png");
        Path tempFilePath = fileSys.getPath(productImagePath + "/product_temp.png");
        Files.copy(originalFilePath, tempFilePath);

        fileService.deleteFile(productImagePath, name);

        File file = new File(productImagePath +"/product_abc.png");
        Assertions.assertFalse(file.exists());

        Files.copy(tempFilePath, originalFilePath);
        Files.delete(tempFilePath);
    }

    @Test
    public void getResource_Test() throws FileNotFoundException {
        String name = "user_abc.png";
        InputStream inputStream = fileService.getResource(userImagePath, name);

        Assertions.assertNotNull(inputStream);
    }
}
