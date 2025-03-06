package com.LaptopWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false, unique = true)
    String orderCode;

    @Column(nullable = false)
    String address;

    String phone;

    Date createdAt;

    Long totalPrice;

    @Column(columnDefinition = "MEDIUMTEXT")
    String note;

    boolean paymentStatus;

    String paymentType;

    String transactionId; // transactionId

    @Column(columnDefinition = "json")
    String callbackPayment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "status_order_id")
    OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderDetail> orderDetails;


    @PrePersist
    public void generateOrderCode() {
        createdAt = new Date();

        if (this.orderCode == null) {
            this.orderCode = UUID.randomUUID().toString();
        }
    }
}
