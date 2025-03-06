package com.LaptopWeb.controller;

import com.LaptopWeb.dto.request.ProductRequest;
import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.ProductResponse;
import com.LaptopWeb.entity.Product;
import com.LaptopWeb.mapper.ProductMapper;
import com.LaptopWeb.service.CategoryService;
import com.LaptopWeb.service.ProductService;
import com.LaptopWeb.utils.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    private static final String PAGE_SIZE = "10";
    private static final String PAGE_NUMBER = "1";
    @Autowired
    private CategoryService categoryService;

    @PostMapping /*checked success*/
    public ResponseEntity<?> createProduct(
            @Valid
            @RequestPart("product") ProductRequest request,
            @RequestPart("images") List<MultipartFile> images
            ) throws Exception {
        Product product = productService.createProduct(request, images);

        ProductResponse productResponse = productMapper.toProductResponse(product);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{id}") /*checked success*/
    public ResponseEntity<?> getProductById(@PathVariable("id") Integer id) {
        Product product = productService.getProductById(id);

        ProductResponse productResponse = productMapper.toProductResponse(product);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping() /*checked success*/
    public ResponseEntity<?> getProductByName(@RequestParam("name") String name) {
        Product product = productService.getProductByName(name);

        ProductResponse productResponse = productMapper.toProductResponse(product);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/all") /*checked success*/
    public ResponseEntity<?> getAllProduct(
        @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
        @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
        @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
        @RequestParam(name = "order", defaultValue = "asc") String order
    ) {
        Page<Product> productPage = productService.getAllProduct(number-1, size, sortBy, order);

        List<Product> products = productPage.getContent();
        List<ProductResponse> productResponses = products.stream().map(productMapper::toProductResponse).toList();

        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getNumberOfElements())
                .totalPages(productPage.getTotalPages())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/allPage") /*checked success*/
    public ResponseEntity<?> getPageProduct(
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "order", defaultValue = "asc") String order,
            @RequestParam(name = "keyWord", required = false) String keyWord,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "minPrice", required = false) Long minPrice,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice
    ) {
        Page<Product> productPage = productService.getPageProduct(number-1, size, sortBy, order,
                keyWord, category, brand, minPrice, maxPrice);

        List<Product> products = productPage.getContent();
        List<ProductResponse> productResponses = products.stream().map(productMapper::toProductResponse).toList();

        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getNumberOfElements())
                .totalPages(productPage.getTotalPages())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/page-category-brand") /*checked success*/
    public ResponseEntity<?> getAllPageProductByCategoryAndBrandId(
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "brandId", required = false) Integer brandId,
            @RequestParam(name = "minPrice", required = false, defaultValue = "0") Long minPrice,
            @RequestParam(name = "maxPrice", required = false, defaultValue = "1000000000") Long maxPrice
    ) {
        Page<Product> productPage = productService.getAllPageProductByCategoryAndBrandId(number-1, size, categoryId,
                brandId, minPrice, maxPrice);
        List<Product> products = productPage.getContent();
        List<ProductResponse> productResponses = products.stream().map(productMapper::toProductResponse).toList();

        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getNumberOfElements())
                .totalPages(productPage.getTotalPages())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/category-brand") /*checked success*/
    public ResponseEntity<?> getAllProductByCategoryAndBrandId(
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "brandId", required = false) Integer brandId,
            @RequestParam(name = "minPrice", required = false, defaultValue = "0") Long minPrice,
            @RequestParam(name = "maxPrice", required = false, defaultValue = "1000000000") Long maxPrice
    ) {
        List<Product> products = productService.getAllProductByCategoryAndBrandId(categoryId, brandId, minPrice, maxPrice);

        List<ProductResponse> productResponses = products.stream().map(productMapper::toProductResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponses)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/brand/{id}") /*checked success*/
    public ResponseEntity<?> getAllProductByBrand(
            @PathVariable("id") Integer id,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "minPrice", defaultValue = "0") Long minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "100000000") Long maxPrice
    ) {
        Page<Product> productPage = productService.getAllProductByBrand(id, number-1, size, minPrice, maxPrice);

        List<Product> products = productPage.getContent();
        List<ProductResponse> productResponses = products.stream().map(productMapper::toProductResponse).toList();

        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getNumberOfElements())
                .totalPages(productPage.getTotalPages())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/search") /*checked success*/
    public ResponseEntity<?> getProductWithKeyword(
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "keyword", required = false) String keyword

    ) {
        Page<Product> productPage = productService.getProductWithKeyword(number-1, size, keyword);

        List<Product> products = productPage.getContent();
        List<ProductResponse> productResponses = products.stream().map(productMapper::toProductResponse).toList();

        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getNumberOfElements())
                .totalPages(productPage.getTotalPages())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/search_product") /*checked success*/
    public ResponseEntity<?> getProductWithKeyword(@RequestParam(name = "keyword", required = false) String keyword) {
        List<Product> products = productService.getProductWithKeywordAll(keyword);
        List<ProductResponse> productResponses = products.stream().map(productMapper::toProductResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponses)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{id}") /*checked success*/
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Integer id,
            @RequestPart(name = "product", required = false) ProductRequest request,
            @RequestPart(name = "newImages", required = false) List<MultipartFile> newImages
    ) throws Exception {
        if (newImages == null) {
            newImages = new ArrayList<>();
        }

        Product product = productService.updateProduct(id, request, newImages);

        ProductResponse productResponse = productMapper.toProductResponse(product);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{id}") /*checked success*/
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Integer id) throws Exception {
        productService.deleteProduct(id);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("Delete product successful")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/add-stock/{productId}") /*checked success*/
    public ResponseEntity<?> addProductStock(
            @PathVariable("productId") Integer productId,
            @RequestParam("stock") int stock,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Product product = productService.addProductStock(productId, stock);

        ProductResponse productResponse = productMapper.toProductResponse(product);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

}
