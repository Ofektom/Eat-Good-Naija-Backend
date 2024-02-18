package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.dto.FavoriteProductDto;
import com.example.eatgoodliveproject.dto.ProductDto;
import com.example.eatgoodliveproject.enums.Category;
import com.example.eatgoodliveproject.exception.ResourceNotFoundException;
import com.example.eatgoodliveproject.model.Product;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.repositories.ProductRepository;
import com.example.eatgoodliveproject.repositories.UserRepository;
import com.example.eatgoodliveproject.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    private String authUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }else{
            throw new RuntimeException("No User found");
        }
    }

    public Users findUserByEmail(String username) {

        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Username Not Found" + username));
    }

    @Override
    public ResponseEntity<String> addProduct(ProductDto productDto) {

        Users user = findUserByEmail(authUser());
        Product product = new ObjectMapper().convertValue(productDto, Product.class);
        Optional<Product> product1 = productRepository.findProductByNameIgnoreCase(product.getName());
        if(product1.isPresent() ){
            return ResponseEntity.ok("Product with name " + product.getName()+ " has already been added");
        }

        product.setUser(user);
        productRepository.save(product);
        return ResponseEntity.ok(product.getName() + " added successfully");
    }




    @Override
    public ResponseEntity<Product> getProductById(Long productId) {
        return new ResponseEntity<>(productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with ID "+ productId + " not found")), HttpStatus.OK);

    }

    @Override
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    @Override
    public Page<Product> findAllPaged(int pageNumber, int pageSizes, String sortBy){
        return productRepository.findAll(PageRequest.of(pageNumber, pageSizes).withSort(Sort.Direction.ASC, sortBy));
    }

    @Override
    public Page<Product> searchByCategory(Category category, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder );
        return productRepository.findAllByCategory(category, pageable);
    }

    @Override
    public Page<Product> searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        return productRepository.findProductByNameLike(keyword, pageable);
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }



    @Override
    public ResponseEntity<String> deleteProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if(!product.isPresent()){
            return new ResponseEntity<>("No such item found", HttpStatus.BAD_REQUEST);
        }
        productRepository.delete(product.get());
        return ResponseEntity.ok(product.get().getName() + "with ID " + id + " is deleted successfully");
    }

    @Override
    public ResponseEntity<?> editProduct(Long productId, ProductDto productDto) {
        Optional<Product> product = productRepository.findById(productId);
        Users user = findUserByEmail(authUser());
        if(!product.isPresent()){
            return new ResponseEntity<>("Product with ID " + productId + " not found", HttpStatus.BAD_REQUEST);
        }
        product.get().setName(productDto.getName());
        product.get().setCategory(productDto.getCategory());
        product.get().setSize(productDto.getSize());
        product.get().setPrice(productDto.getPrice());
        product.get().setImageUrl("Img.jpeg");
        product.get().setUser(user);
        productRepository.save(product.get());

        return ResponseEntity.ok("Product updated successfully");
    }

    @Override
    public ResponseEntity<?> productIsAvailable(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
       product.setAvailable(true);
       productRepository.save(product);
        return ResponseEntity.ok("Product is now Available");
    }

    @Override
    public ResponseEntity<String> productIsNotAvailable(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setAvailable(false);
        productRepository.save(product);
        return ResponseEntity.ok("Out of Stock");
    }

    @Override
    public List<Product> getAllFavoriteProducts(Long userId) {
        return(productRepository.findByUserId(userId));
    }


    @Override
    public void addFavoriteProduct(Long productId, Users user) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product does not Exist"));
        boolean favorite = false;
        for (Users user1 : product.getFavoriteBy()) {
            if (user1.getUsername().equals(user.getUsername())) {
                favorite = true;
                break;
            }
        }
        if (!favorite) {
            product.getFavoriteBy().add(user);
            product.setFavorite(product.getFavorite() + 1);
        }
        productRepository.saveAndFlush(product);
    }

    @Override
    public void removeFavoriteProduct(Long productId, Users user) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product does not Exist"));
        for (Users user1 : product.getFavoriteBy()) {
            if (user1.getUsername().equals(user.getUsername())) {
                product.getFavoriteBy().remove(user1);
                product.setFavorite(product.getFavorite() - 1);
                break;
            }
        }
        productRepository.saveAndFlush(product);
    }

    @Override
    public FavoriteProductDto getFavoriteProduct(Long productId, Users user) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product does not Exist"));

        for (Users user1 : product.getFavoriteBy()) {
            if (user1.getUsername().equals(user.getUsername())) {
                return FavoriteProductDto.builder()
                        .name(product.getName())
                        .category(product.getCategory())
                        .price(product.getPrice())
                        .size(product.getSize())
                        .favorites(product.getFavorite())
                        .isFavorite(true)
                        .build();
            }
        }

        return FavoriteProductDto.builder()
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .size(product.getSize())
                .favorites(product.getFavorite())
                .isFavorite(false)
                .build();
    }



}
