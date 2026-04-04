package io.github.ngtrphuc.smartphone_shop.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import io.github.ngtrphuc.smartphone_shop.service.AuthService;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null)  model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        return "customer/auth/login"; // templates/customer/auth/login.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "customer/auth/register"; 
    }

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String fullName,
                           @RequestParam String password, Model model) {
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "customer/auth/register";
        }
        boolean success = authService.register(email, fullName, password);
        if (!success) {
            model.addAttribute("error", "Email already exists.");
            return "customer/auth/register";
        }
        return "redirect:/login?registered";
    }
}