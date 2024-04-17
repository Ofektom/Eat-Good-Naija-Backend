package com.example.eatgoodliveproject.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.core.io.Resource;


public interface StorageService {
    String store(MultipartFile file) throws IOException;
    String storeFile(MultipartFile file);
    Resource loadFile(String fileName);
}

