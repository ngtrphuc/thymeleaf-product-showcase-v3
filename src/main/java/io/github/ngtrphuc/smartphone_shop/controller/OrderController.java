package io.github.ngtrphuc.smartphone_shop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.ngtrphuc.smartphone_shop.service.OrderService;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/my-orders")
    public String myOrders(Authentication auth, Model model) {
        model.addAttribute("orders", orderService.getOrdersByUser(auth.getName()));
        model.addAttribute("shopname", "Smartphone Shop");
        model.addAttribute("address", "Asaka, Saitama, Japan");
        return "my-orders";
    }
}