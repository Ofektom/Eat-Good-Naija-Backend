package com.example.eatgoodliveproject.controller;

import com.example.eatgoodliveproject.config.AppConstants;
import com.example.eatgoodliveproject.dto.FavoriteProductDto;
import com.example.eatgoodliveproject.dto.PagedResponse;
import com.example.eatgoodliveproject.dto.ProductDto;
import com.example.eatgoodliveproject.enums.Category;
import com.example.eatgoodliveproject.exception.ResourceNotFoundException;
import com.example.eatgoodliveproject.model.Product;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final PagedListHolder<Product> pagedListHolder;
    private List<Product> productList;

    @Autowired
    public ProductController(ProductService productService, PagedListHolder<Product> pagedListHolder) {
        this.productService = productService;
        this.pagedListHolder = pagedListHolder;
    }

    @PostMapping("/vendor/add")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<String> addProduct(@RequestBody ProductDto productDto){
        return productService.addProduct(productDto);
    }

    @PutMapping("/vendor/edit-product/{productId}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductDto productDto){
        return ResponseEntity.ok(productService.editProduct(productId, productDto));
    }

    @GetMapping("/user/view/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId){
        return productService.getProductById(productId);
    }

    @GetMapping("/user/view/all")
    public ResponseEntity<List<Product>> findALl(){
        List<Product> products = productService.findAll();
        productList = products;
        return ResponseEntity.ok(products);
    }

    @GetMapping("/user/view-paginated/{pageNo}")
    public ResponseEntity<PagedResponse<Product>> paginatedView(@PathVariable int pageNo){
        pagedListHolder.setPage(pageNo);
        pagedListHolder.setSource(productList);
        PagedResponse<Product> productPagedResponse = new PagedResponse<>();
        productPagedResponse.setPagedList(pagedListHolder.getPageList());
        productPagedResponse.setPageNo(pagedListHolder.getPage());
        productPagedResponse.setPageSize(pagedListHolder.getPageSize());
        productPagedResponse.setTotalSize(pagedListHolder.getNrOfElements());
        return new ResponseEntity<>(productPagedResponse, HttpStatus.OK);
    }

    @GetMapping("/user/view/all-paged")
    public ResponseEntity<Page<Product>> findAllPaged(
            @RequestParam(required = false, name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER)String pageNumber,
            @RequestParam(required = false, name = "pageSize", defaultValue = AppConstants.PAGE_SIZE)String pageSize,
            @RequestParam(required = false, name = "sortBy", defaultValue = "category")String sortBy){
    Page<Product> productPage = productService.findAllPaged(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), sortBy);
    return ResponseEntity.ok(productPage);
    }

    @GetMapping("/user/view/category/{category}")
    public ResponseEntity<Page<Product>> findByPagedCategory(
            @PathVariable Category category,
            @RequestParam(required = false, name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER)Integer pageNumber,
            @RequestParam(required = false, name = "pageSize", defaultValue = AppConstants.PAGE_SIZE)Integer pageSize,
            @RequestParam(required = false, name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY)String sortBy,
            @RequestParam(required = false,name = "sortOrder", defaultValue = AppConstants.SORT_DIR)String sortOrder){
        Page<Product> productPage = productService.searchByCategory(category, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productPage);
    }

    @GetMapping("/user/view/keyword/{keyword}")
    public ResponseEntity<Page<Product>> findByKeyword(
            @PathVariable String keyword,
            @RequestParam(required = false, name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER)Integer pageNumber,
            @RequestParam(required = false, name = "pageSize", defaultValue = AppConstants.PAGE_SIZE)Integer pageSize,
            @RequestParam(required = false, name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY)String sortBy,
            @RequestParam(required = false,name = "sortOrder", defaultValue = AppConstants.SORT_DIR)String sortOrder){
        Page<Product> productPage = productService.searchByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productPage);
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable Category category) {
        return productService.getProductsByCategory(category);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        return productService.deleteProductById(id);
    }

    @GetMapping("/all/favorite-products")
    public ResponseEntity<List<Product>> getAllFavoritesProducts(@PathVariable Long userId){
        List<Product> favoriteProducts = productService.getAllFavoriteProducts(userId);
        return new ResponseEntity<>(favoriteProducts, HttpStatus.OK);

    }

    @PostMapping("/add-favoriteProduct/{productId}")
    public ResponseEntity<String> addFavoriteProduct (@PathVariable(name = "productId") Long productId,
                                                      @AuthenticationPrincipal Users currentUser) throws ResourceNotFoundException {
        productService.addFavoriteProduct(productId, currentUser);
        return new ResponseEntity<>("You Added Favorite Product", HttpStatus.OK);
    }


    @PostMapping("/remove-favoriteProduct/{productId}")
    public ResponseEntity<String> removeFavoriteProduct (@PathVariable(name = "productId") Long productId,
                                                         @AuthenticationPrincipal Users currentUser) throws ResourceNotFoundException {
        productService.removeFavoriteProduct(productId, currentUser);
        return new ResponseEntity<>("You Removed Favorite Product", HttpStatus.OK);
    }


    @GetMapping("/get-favoriteProduct/{productId}")
    public ResponseEntity<FavoriteProductDto> getFavoriteProduct(@PathVariable(name = "productId") Long productId, @AuthenticationPrincipal Users currentUser) throws ResourceNotFoundException {
        FavoriteProductDto response = productService.getFavoriteProduct(productId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/product-available/{productId}")
    public ResponseEntity<?> productAvailable(@PathVariable Long productId){
        return productService.productIsAvailable(productId);
    }

    @PutMapping("/out-of-stock/{productId}")
    public ResponseEntity<?> productIsNotAvailable(@PathVariable Long productId){
        return productService.productIsNotAvailable(productId);
    }
}
