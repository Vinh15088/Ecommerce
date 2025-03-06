package com.LaptopWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @CreationTimestamp
    @Column(nullable = false)
    Date createdAt;

    @Column(nullable = false)
    String comment;

    @Column(nullable = false)
    Integer rating;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;
}
