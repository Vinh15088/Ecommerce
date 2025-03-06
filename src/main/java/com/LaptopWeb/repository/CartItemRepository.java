package com.LaptopWeb.repository;

import com.LaptopWeb.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.product.id = ?1 AND ci.cart.id = ?2")
    CartItem findCartItemByProductIdAndCartId(Integer productId, Integer cartId);

    void deleteCartItemByProductIdAndCartId(Integer productId, Integer cartId);

    void deleteAllCartItemByCartId(Integer cartId);
}
