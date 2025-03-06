package com.LaptopWeb.utils;

import com.LaptopWeb.entity.OrderStatus;
import com.LaptopWeb.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

//@Component
//public class AddStatusOrder implements CommandLineRunner {
//    @Autowired
//    private OrderStatusRepository orderStatusRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        OrderStatus pending = new OrderStatus("PENDING", "Order is pending approval");
//        OrderStatus confirmed = new OrderStatus("CONFIRMED", "Order has been confirmed");
//        OrderStatus shipped = new OrderStatus("SHIPPED", "Order has been shipped");
//        OrderStatus delivered = new OrderStatus("DELIVERED", "Order has been delivered");
//        OrderStatus canceled = new OrderStatus("CANCELED", "Order has been canceled");
//
//        orderStatusRepository.saveAll(Arrays.asList(pending, confirmed, shipped, delivered, canceled));
//    }
//}
