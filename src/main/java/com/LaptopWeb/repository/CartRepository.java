package com.LaptopWeb.repository;

import com.LaptopWeb.entity.Cart;
import com.LaptopWeb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.user.id = ?1")
    Cart findByUserId(Integer id);

    Cart findByUser(User user);

    boolean existsByUserId(Integer id);

}
