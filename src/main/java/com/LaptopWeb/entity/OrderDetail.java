package com.LaptopWeb.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_detail")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int unitPrice;
    int quantity;
    int totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

}
