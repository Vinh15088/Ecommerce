package com.LaptopWeb.controller;

import com.LaptopWeb.dto.request.ReviewRequest;
import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.ReviewResponse;
import com.LaptopWeb.entity.Review;
import com.LaptopWeb.mapper.ReviewMapper;
import com.LaptopWeb.service.ReviewService;
import com.LaptopWeb.utils.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private static final String SLICE_NUMBER = "1";
    private static final String SLICE_SIZE = "5";

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

    @PostMapping /*checked success*/
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest request, @AuthenticationPrincipal Jwt jwt) {
//        Map<String, Object> data = (Map<String, Object>) jwt.getClaim("data");
//
//        String username = (String) data.get("username");

        String username = (String) jwt.getClaimAsMap("data").get("username");

        Review review = reviewService.createReview(username, request);

        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{reviewId}") /*checked success*/
    public ResponseEntity<?> getReviewById(@PathVariable("reviewId") Integer reviewId) {
        Review review = reviewService.getReviewById(reviewId);

        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/byProduct") /*checked success*/
    public ResponseEntity<?> getReviewByRating(
            @RequestParam(name = "productId") Integer productId,
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "order", defaultValue = "desc") String order
    ) {
        List<Review> list = reviewService.getByRatingAndProduct(rating, productId, sortBy, order);

        List<ReviewResponse> reviewResponses = list.stream().map(reviewMapper::toReviewResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponses)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("{productId}/byUser") /*checked success*/
    public ResponseEntity<?> getReviewByUser(
            @PathVariable(name = "productId") Integer productId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = (String) jwt.getClaimAsMap("data").get("username");

        Review review = reviewService.getByUserAndProduct(username, productId);

        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/update/{reviewId}") /*checked success*/
    public ResponseEntity<?> updateReview(
            @PathVariable(name = "reviewId") Integer reviewId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Map<String, Object> data = (Map<String, Object>) jwt.getClaim("data");

        String username = (String) data.get("username");

        Review review = reviewService.updateReview(reviewId, request, username);

        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{reviewId}") /*checked success*/
    public ResponseEntity<?> deleteReview(@PathVariable(name = "reviewId") Integer reviewId) {
        reviewService.deleteReview(reviewId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("Delete review successful")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

}
