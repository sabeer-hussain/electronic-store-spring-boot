package com.sabeer.electronic.store.services.impl;

import com.sabeer.electronic.store.exceptions.BadApiRequestException;
import com.sabeer.electronic.store.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        // abc.png
        String originalFilename = file.getOriginalFilename();
        LOGGER.info("Filename : {}", originalFilename);
        String fileName = UUID.randomUUID().toString();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileNameWithExtension = fileName + extension;
        String fullPathWithFileName = path + fileNameWithExtension;
        LOGGER.info("Full image path : {}", fullPathWithFileName);

        if (extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg")
                || extension.equalsIgnoreCase(".jpeg")) {
            // file save
            LOGGER.info("File extension is : {}", extension);
            File folder = new File(path);
            if (!folder.exists()) {
                // create the folder
                folder.mkdirs();
            }

            // upload file
            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));
            return fileNameWithExtension;
        } else {
            throw new BadApiRequestException("File with this " + extension + " not allowed !!");
        }
    }
}
