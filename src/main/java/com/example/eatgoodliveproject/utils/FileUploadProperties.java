package com.example.eatgoodliveproject.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class FileUploadProperties {

    @Value("${file.upload-dir}")
    private String uploadDir;
}

