package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.ProductRequest;
import com.LaptopWeb.entity.Brand;
import com.LaptopWeb.entity.Category;
import com.LaptopWeb.entity.Product;
import com.LaptopWeb.entity.ProductImage;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.mapper.ProductMapper;
import com.LaptopWeb.repository.ProductImageRepository;
import com.LaptopWeb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@EnableMethodSecurity
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AwsS3Service awsS3Service;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductImageRepository productImageRepository;


    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(ProductRequest request, List<MultipartFile> imageProducts) throws Exception {
        if (productRepository.existsByName(request.getName())) {
            throw  new AppException(ErrorApp.PRODUCT_NAME_EXISTED);
        }

        Product product = productMapper.toProduct(request);

        if(request.getCategory_id() != null) {
            Category category = categoryService.getCategoryById(request.getCategory_id());

            product.setCategory(category);
        }

        if(request.getBrand_id() != null) {
            Brand brand = brandService.getById(request.getBrand_id());

            product.setBrand(brand);
        }

        product.setCreatedAt(new Date());
        product.setUpdatedAt(null);

        Product savedProduct = productRepository.save(product);

        List<ProductImage> images = new ArrayList<>();
        String folder = "products/" + savedProduct.getId();

        for(MultipartFile imageProduct: imageProducts) {
            String imageUrl = awsS3Service.saveImageToS3(imageProduct, folder);

            ProductImage productImage = new ProductImage();
            productImage.setUrl(imageProduct.getOriginalFilename());
            productImage.setProduct(savedProduct);
            images.add(productImage);
        }

        savedProduct.setImages(images);
        return productRepository.save(savedProduct);
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.PRODUCT_NOT_FOUND));
    }

    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }

    public Page<Product> getAllProduct(Integer number, Integer size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        return productRepository.findAll(pageable);
    }

    public Page<Product> getPageProduct(Integer number, Integer size, String sortBy, String order,
                                        String keyWord, String category, String brand, Long minPrice, Long maxPrice) {
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        return productRepository.findPageProduct(keyWord, category, brand, minPrice, maxPrice, pageable);
    }

    public Page<Product> getAllPageProductByCategoryAndBrandId(Integer number, Integer size, Integer categoryId,
                                                               Integer brandId, Long minPrice, Long maxPrice) {
        Pageable pageable = PageRequest.of(number, size);
        Category category = categoryService.getCategoryById(categoryId);
        Set<Category> categories = category.getChildren();

        if(categories != null && categories.size() > 0) {
            List<Page<Product>> productPages = new ArrayList<>();
            for(Category c: categories) {
                System.out.println(c.getId());
                Page<Product> page = productRepository.findAllPageWithCategoryIdAndBrandId(c.getId(), brandId,
                        minPrice, maxPrice, pageable);
                productPages.add(page);
            }
            return mergePages(productPages, pageable);
        } else {
            return productRepository.findAllPageWithCategoryIdAndBrandId(categoryId, brandId, minPrice, maxPrice, pageable);
        }
    }

    private Page<Product> mergePages(List<Page<Product>> pageProducts, Pageable pageable) {
        List<Product> allProducts = new ArrayList<>();
        for (Page<Product> page : pageProducts) {
            allProducts.addAll(page.getContent());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allProducts.size());

        if (start >= allProducts.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allProducts.size());
        }

        List<Product> pagedProducts = allProducts.subList(start, end);

        return new PageImpl<>(pagedProducts, pageable, allProducts.size());
    }

    public List<Product> getAllProductByCategoryAndBrandId(Integer categoryId, Integer brandId, Long minPrice, Long maxPrice) {
        List<Product> products = new ArrayList<>();
        Category category = categoryService.getCategoryById(categoryId);

        Set<Category> categories = category.getChildren();

        if(categories != null && categories.size() > 0) {
            for(Category c: categories) {
                System.out.println(c.getId());
                products.addAll(productRepository.findAllWithCategoryIdAndBrandId(c.getId(), brandId, minPrice, maxPrice));
            }
            System.out.println(products.size());
        } else {
            products.addAll(productRepository.findAllWithCategoryIdAndBrandId(categoryId, brandId, minPrice, maxPrice));
        }

        return products;
    }

    public Page<Product> getAllProductByBrand(Integer brandId, Integer number, Integer size, Long minPrice, Long maxPrice) {
        Brand brand = brandService.getById(brandId);

        Pageable pageable = PageRequest.of(number, size);

        return productRepository.findAllWithBrandId(brandId, minPrice, maxPrice, pageable);
    }

    public Page<Product> getProductWithKeyword(Integer number, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(number, size);

        return (keyword != null && !keyword.isEmpty())
                ? productRepository.findAll(keyword, pageable)
                : productRepository.findAll(pageable);
    }

    public List<Product> getProductWithKeywordAll(String keyword) {
        return (keyword != null && !keyword.isEmpty()) ? productRepository.findAllProduct(keyword) : null;
    }

    public List<Product> getProductsByIds(List<Integer> productIds) {
        return productRepository.findAllById(productIds);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProduct(Integer id, ProductRequest request, List<MultipartFile> newImages) throws Exception {
        Product product = getProductById(id);

        if(!request.getName().equals(product.getName()) &&
            productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorApp.PRODUCT_NAME_EXISTED);
        }

        Product product1 = productMapper.toProduct(request);

        if(request.getCategory_id() != null) {
            Category category = categoryService.getCategoryById(request.getCategory_id());

            product1.setCategory(category);
        }

        if(request.getBrand_id() != null) {
            Brand brand = brandService.getById(request.getBrand_id());

            product1.setBrand(brand);
        }

        product1.setId(product.getId());
        product1.setCreatedAt(product.getCreatedAt());
        product1.setUpdatedAt(new Date());

        product1.setImages(new ArrayList<>(product.getImages()));

        // process delete image
        if(request.getImagesToDelete() != null) {
            String folder = "products/" + id;

            Iterator<ProductImage> images = product1.getImages().iterator();
            while(images.hasNext()) {
                ProductImage productImage = images.next();

                if(request.getImagesToDelete().contains(productImage.getUrl())) {
                    awsS3Service.deleteImageFromS3(folder, productImage.getUrl());

                    images.remove();
                    productImageRepository.delete(productImage);
                }
            }
        }

        // process add image
        String folder = "products/" + id;
        if(newImages != null && !newImages.isEmpty()) {
            for(MultipartFile image: newImages) {
                String imageUrl = awsS3Service.saveImageToS3(image, folder);

                ProductImage productImage = new ProductImage();
                productImage.setUrl(image.getOriginalFilename());
                productImage.setProduct(product1);

                product1.getImages().add(productImage);
            }
        }

        return productRepository.save(product1);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Integer id) throws Exception {
        Product product = getProductById(id);

        String folder = "products/" + id;

        awsS3Service.deleteImageFromS3Folder(folder);

        productRepository.deleteById(id);
    }

//    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
    public Product addProductStock(Integer id, int stock) {
        Product product = getProductById(id);

        product.setStock(product.getStock() + stock);

        return productRepository.save(product);
    }


}
