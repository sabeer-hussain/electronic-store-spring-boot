package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.exceptions.BadApiRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imagePath;

    // get resource
    @Test
    public void uploadFileTest() throws IOException {
        String name = "user_abc.png";
        String originalFileName = "user_abc.png";
        String contentType = "image/jpeg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(imagePath+ name));
        } catch (final IOException e) {
        }

        MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

        String uploadedFileName = fileService.uploadFile(multipartFile, name);

        Assertions.assertTrue(uploadedFileName.endsWith(".png"));
    }

    @Test
    public void uploadFile_BadApiRequestException_Test() {
        String name = "user_abc.txt";
        String originalFileName = "user_abc.txt";
        String contentType = "image/jpeg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(imagePath+ name));
        } catch (final IOException e) {
        }

        MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

        Assertions.assertThrows(BadApiRequestException.class, () -> fileService.uploadFile(multipartFile, name));

    }

    // get resource
    @Test
    public void getResourceTest() throws FileNotFoundException {
        String name = "user_abc.png";
        InputStream inputStream = fileService.getResource(imagePath, name);

        Assertions.assertNotNull(inputStream);
    }
}
