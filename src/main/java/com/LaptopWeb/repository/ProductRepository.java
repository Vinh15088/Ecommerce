package com.LaptopWeb.repository;

import com.LaptopWeb.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    boolean existsByName(String name);

    Product findByName(String name);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN p.brand b " +
            "WHERE p.name LIKE %?1% " +
            "OR (c IS NOT NULL AND c.name LIKE %?1%) " +
            "OR (b IS NOT NULL AND b.name LIKE %?1%)")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN p.brand b " +
            "WHERE p.name LIKE %?1% " +
            "OR (c IS NOT NULL AND c.name LIKE %?1%) " +
            "OR (b IS NOT NULL AND b.name LIKE %?1%)")
    List<Product> findAllProduct(String keyword);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN p.brand b " +
            "WHERE (:keyWord IS NULL OR " +
            "(p.name LIKE %:keyWord% OR " +
            "p.sku LIKE %:keyWord% OR " +
            "c.name LIKE %:keyWord% OR " +
            "b.name LIKE %:keyWord%)) " +
            "AND (:category IS NULL OR c.name = :category) " +
            "AND (:brand IS NULL OR b.name = :brand) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findPageProduct(@Param("keyWord") String keyWord,
                                  @Param("category") String category,
                                  @Param("brand") String brand,
                                  @Param("minPrice") Long minPrice,
                                  @Param("maxPrice") Long maxPrice,
                                  Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (?1 IS NULL OR p.category.id = ?1) " +
            "AND (?2 IS NULL OR p.brand.id = ?2) " +
            "AND p.price >= ?3 AND p.price <= ?4")
    Page<Product> findAllPageWithCategoryIdAndBrandId(Integer categoryId, Integer brandId, Long minPrice, Long maxPrice,
                                                      Pageable pageable);


    @Query("SELECT p FROM Product p WHERE (?1 IS NULL OR p.category.id = ?1) " +
            "AND (?2 IS NULL OR p.brand.id = ?2) " +
            "AND p.price >= ?3 AND p.price <= ?4")
    List<Product> findAllWithCategoryIdAndBrandId(Integer categoryId, Integer brandId, Long minPrice, Long maxPrice);


    @Query("SELECT p FROM Product p WHERE p.brand.id = ?1 " +
            "AND p.price >= ?2 AND p.price <= ?3")
    Page<Product> findAllWithBrandId(Integer id, Long minPrice, Long maxPrice, Pageable pageable);
}

