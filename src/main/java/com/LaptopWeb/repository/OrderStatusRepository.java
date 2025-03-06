package com.LaptopWeb.repository;

import com.LaptopWeb.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {

    Optional<OrderStatus> findByName(String name);

}
