package com.LaptopWeb.repository;

import com.LaptopWeb.entity.Order;
import com.LaptopWeb.entity.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByUserId(int userId);

    Optional<Order> findByTransactionId(String transactionId);

    boolean existsByOrderCode(String orderCode);

    boolean existsByTransactionId(String transactionId);

    @Query("SELECT COUNT(o) > 0 FROM  Order o WHERE o.orderCode = ?1 AND o.transactionId = ?2")
    boolean existsByOrderCodeAndTransactionId(String orderCode, String transactionId);

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = ?1 WHERE o.orderCode = ?2")
    int updateOrderStatus(OrderStatus orderStatus, String orderCode);

    @Query("SELECT o FROM Order o WHERE o.user.username = ?1")
    List<Order> getAllOrderByUser(String username);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN o.orderStatus oS " +
            "LEFT JOIN o.user u " +
            "WHERE (:keyWord IS NULL OR " +
            "(u.username LIKE %:keyWord% OR " +
            "u.fullName LIKE %:keyWord%)) " +
            "AND (:status IS NULL OR oS.name = :status) " +
            "AND (:method IS NULL OR o.paymentType = :method)")
    Page<Order> findAll(@Param("keyWord") String keyword,
                        @Param("status") String status,
                        @Param("method") String method,
                        Pageable pageable);


}
