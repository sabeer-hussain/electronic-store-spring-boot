package com.sabeer.electronic.store.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadFile(MultipartFile file, String path) throws IOException;
}
