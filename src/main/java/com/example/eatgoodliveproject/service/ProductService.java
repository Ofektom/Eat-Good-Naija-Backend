package com.example.eatgoodliveproject.service;

import com.example.eatgoodliveproject.dto.FavoriteProductDto;
import com.example.eatgoodliveproject.dto.ProductDto;
import com.example.eatgoodliveproject.enums.Category;
import com.example.eatgoodliveproject.exception.ResourceNotFoundException;
import com.example.eatgoodliveproject.model.Product;
import com.example.eatgoodliveproject.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    FavoriteProductDto getFavoriteProduct(Long productId, Users user) throws ResourceNotFoundException;
    void removeFavoriteProduct(Long productId, Users user) throws ResourceNotFoundException;
    void addFavoriteProduct(Long productId, Users user) throws ResourceNotFoundException;
    List<Product> getAllFavoriteProducts(Long userId);
    ResponseEntity<String> addProduct(ProductDto productDto);
    ResponseEntity<Product> getProductById(Long productId);

    List<Product> findAll();

    Page<Product> findAllPaged(int offset, int pageSizes, String sortBy);

    Page<Product> searchByCategory(Category category, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    Page<Product> searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ResponseEntity<String> deleteProductById(Long id);

    ResponseEntity<?> editProduct(Long productId, ProductDto productDto);

    ResponseEntity<?> productIsAvailable(Long productId);
    ResponseEntity<String> productIsNotAvailable(Long productId);
    List<Product> getProductsByCategory(Category category);
}
