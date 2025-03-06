package com.LaptopWeb.controller;

import com.LaptopWeb.dto.request.CartItemRequest;
import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.CartResponse;
import com.LaptopWeb.entity.Cart;
import com.LaptopWeb.entity.Product;
import com.LaptopWeb.mapper.CartMapper;
import com.LaptopWeb.repository.ProductRepository;
import com.LaptopWeb.service.CartService;
import com.LaptopWeb.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductService productService;



    @PostMapping("/add-to-cart") /*checked success*/
    public ResponseEntity<?> addToCart(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = jwt.getClaimAsMap("data").get("username").toString();

        Cart cart = cartService.addCartItem(request, username);

        CartResponse  cartResponse = cartMapper.toCartResponse(cart);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(cartResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/my-cart") /*checked success*/
    public ResponseEntity<?> getMyCart(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsMap("data").get("username").toString();

        Cart cart = cartService.getMyCart(username);

        CartResponse  cartResponse = cartMapper.toCartResponse(cart);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(cartResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/change-quantity/{cartId}") /*checked success*/
    public ResponseEntity<?> changeQuantity(
            @PathVariable("cartId") Integer cartId,
            @RequestBody CartItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = jwt.getClaimAsMap("data").get("username").toString();

        Cart cart = cartService.updateQuantity(request, username, cartId);

        CartResponse  cartResponse = cartMapper.toCartResponse(cart);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(cartResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/delete-product-from-cart") /*checked success*/
    public ResponseEntity<?> deleteProductFromCart(
            @RequestParam("productId") Integer productId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = jwt.getClaimAsMap("data").get("username").toString();

        Product product = productService.getProductById(productId);

        cartService.deleteProductById(productId, username);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("Deleted product " + product.getName() + " from cart")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/delete-all-product-from-cart") /*checked success*/
    public ResponseEntity<?> deleteAllProductFromCart(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = jwt.getClaimAsMap("data").get("username").toString();

        cartService.deleteAllProductByUsername(username);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("Deleted all products from cart")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }
}
