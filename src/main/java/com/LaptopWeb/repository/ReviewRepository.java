package com.LaptopWeb.repository;

import com.LaptopWeb.entity.Product;
import com.LaptopWeb.entity.Review;
import com.LaptopWeb.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByUserAndProduct(User user, Product product);

    Review findByUserAndProduct(User user, Product product);

    @Query("SELECT r FROM Review r WHERE r.product.id = ?1")
    List<Review> findByProduct(Integer productId, Sort sort);

    @Query("SELECT r FROM Review r WHERE r.rating = ?1")
    Slice<Review> findByRating(Integer rating, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.rating = ?1 AND r.product.id = ?2")
    List<Review> findByRatingAndProduct(Integer rating, Integer productId, Sort sort);
}
