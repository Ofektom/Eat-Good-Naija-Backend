package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.exception.StorageException;
import com.example.eatgoodliveproject.exception.StorageFileNotFoundException;
import com.example.eatgoodliveproject.service.StorageService;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;


@Service
public class StorageServiceImpl implements StorageService {
    private final Path fileStorageLocation;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public StorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new StorageException("Could not create the directory for file storage", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new StorageException("Could not store file: " + fileName, ex);
        }
    }

    @Override
    public Resource loadFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                System.out.println(resource);
                return resource;
            } else {
                throw new StorageFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new StorageFileNotFoundException("File not found " + fileName, ex);
        }
    }

//    @Override
//    public byte[] store(MultipartFile file) throws IOException {
//        try {
//            return file.getBytes();
//        } catch (IOException ex) {
//            throw new IOException("Could not store file. Please try again!", ex);
//        }
//    }


    @Override
    public String store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(fileName);
            return fileName;
        } catch (IOException ex) {
            throw new IOException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

}
