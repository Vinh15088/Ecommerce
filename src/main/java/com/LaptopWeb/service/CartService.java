package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.CartItemRequest;
import com.LaptopWeb.entity.Cart;
import com.LaptopWeb.entity.CartItem;
import com.LaptopWeb.entity.Product;
import com.LaptopWeb.entity.User;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.repository.CartItemRepository;
import com.LaptopWeb.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;
    private final CartItemRepository cartItemRepository;


    public Cart addCartItem(CartItemRequest cartItemRequest, String username) {
        User user = userService.getByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        Product product = productService.getProductById(cartItemRequest.getProductId());

        if(cart == null) {
            Cart newCart = new Cart();

            newCart.setUser(user);

            CartItem cartItem = CartItem.builder()
                    .quantity(cartItemRequest.getQuantity())
                    .cart(newCart)
                    .product(product)
                    .build();

            newCart.setCartItems(List.of(cartItem));

            return cartRepository.save(newCart);
        } else {
            CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(product.getId(), cart.getId());

            if(cartItem == null) {
                CartItem newCartItem = CartItem.builder()
                        .quantity(cartItemRequest.getQuantity())
                        .cart(cart)
                        .product(product)
                        .build();

                cartItemRepository.save(newCartItem);
            } else {
                cartItem.addQuantity(cartItemRequest.getQuantity());

                cartItemRepository.save(cartItem);
            }

            return cartRepository.findByUser(user);
        }
    }

    public Cart updateQuantity(CartItemRequest cartItemRequest, String username, Integer cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() ->
                new AppException(ErrorApp.CART_NOT_FOUND));

        if(!cart.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorApp.ACCESS_DENIED);
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartItemRequest.getProductId(), cartId);

        if(cartItem == null) {
            throw new AppException(ErrorApp.CART_ITEM_NOT_FOUND);
        } else {
            if(cartItem.getQuantity() + cartItemRequest.getQuantity() <= 0) {
                cartItemRepository.delete(cartItem);
            } else {
                cartItem.addQuantity(cartItemRequest.getQuantity());

                cartItemRepository.save(cartItem);
            }
        }

        return getMyCart(username);
    }

    public Cart getMyCart(String username) {
        User user = userService.getByUsername(username);
        Cart cart = cartRepository.findByUser(user);

        if(cart == null) {
            throw new AppException(ErrorApp.CART_EMPTY);
        }

        return cart;
    }

    @Transactional
    public void deleteProductById(Integer productId, String username) {
        Cart cart = getMyCart(username);

        Product product = productService.getProductById(productId);

        cartItemRepository.deleteCartItemByProductIdAndCartId(productId, cart.getId());
    }

    @Transactional
    public void deleteAllProductByUsername(String username) {
        Cart cart = getMyCart(username);

        cartItemRepository.deleteAllCartItemByCartId(cart.getId());
    }
}
