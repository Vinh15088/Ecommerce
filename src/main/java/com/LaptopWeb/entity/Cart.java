package com.LaptopWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "carts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    List<CartItem> cartItems;

    public void addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
    }

    public void removeItem(Integer cartItemId){
        this.cartItems.removeIf(cartItem -> cartItem.getId().equals(cartItemId));
    }
}
