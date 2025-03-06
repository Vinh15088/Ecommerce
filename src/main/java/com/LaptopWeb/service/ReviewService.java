package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.ReviewRequest;
import com.LaptopWeb.entity.Product;
import com.LaptopWeb.entity.Review;
import com.LaptopWeb.entity.User;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.mapper.ReviewMapper;
import com.LaptopWeb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final ProductService productService;

    @PreAuthorize("hasRole('USER')")
    public Review createReview(String username, ReviewRequest request) {
        User user = userService.getByUsername(username);

        Product product = productService.getProductById(request.getProduct_id());

        if(reviewRepository.existsByUserAndProduct(user, product)) {
            throw new AppException(ErrorApp.REVIEW_EXISTED);
        }

        Review review = reviewMapper.toReview(request);

        review.setUser(user);
        review.setProduct(product);

        return reviewRepository.save(review);
    }

    public Review getReviewById(Integer id) {
        return reviewRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.REVIEW_NOT_FOUND));
    }

    public Review getByUserAndProduct(String username, Integer productId){
        User user = userService.getByUsername(username);

        Product product = productService.getProductById(productId);

        return reviewRepository.findByUserAndProduct(user, product);
    }

    public List<Review> getByRatingAndProduct(Integer rating, Integer productId, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        if(rating == null) {
            return reviewRepository.findByProduct(productId, sort);
        } else {
            return reviewRepository.findByRatingAndProduct(rating, productId, sort);
        }
    }

    public Review updateReview(Integer id, ReviewRequest request, String username) {
        Review review = getReviewById(id);

        if(!review.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorApp.REVIEW_ACCESS_DENIED);
        }

        review.setComment(request.getComment());
        review.setRating(request.getRating());

        return reviewRepository.save(review);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReview(Integer id) {
        reviewRepository.deleteById(id);
    }


}
