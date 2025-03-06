package com.LaptopWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    @Column(length = 64, unique = true, nullable = false)
    String sku;

    @Column(columnDefinition = "MEDIUMTEXT")
    String shortDescription;

    @Column(columnDefinition = "MEDIUMTEXT")
    String longDescription;

    Integer stock;
    boolean inStock;
    Integer salesNumber;
    Long cost;
    Long price;
    Integer discount;

    Date createdAt;
    Date updatedAt;
    boolean active;

    Double rating;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> images = new ArrayList<>();

    @Column(columnDefinition = "json")
    String productDetails;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    Brand brand;
}
