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
@Table(name = "cart_item")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int quantity;

    Long price;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return  true;
        if(o == null || getClass() != o.getClass()) return false;

        CartItem cartItem = (CartItem) o;
        return id.equals(cartItem.id);
    }

    @PrePersist
    @PreUpdate
    public void price() {
        if(product != null) this.price = this.product.getPrice() * this.quantity;
    }

}
