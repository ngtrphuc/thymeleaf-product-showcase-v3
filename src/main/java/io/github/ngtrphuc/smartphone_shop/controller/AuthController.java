package io.github.ngtrphuc.smartphone_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        if (error != null)  model.addAttribute("error", "Incorrect email or password.");
        if (logout != null) model.addAttribute("message", "Logged out successfully.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String fullName,
                           @RequestParam String password,
                           Model model) {
        if (password.length() < 6) {
            model.addAttribute("error", "The password must be at least 6 characters long.");
            return "auth/register";
        }
        boolean success = authService.register(email, fullName, password);
        if (!success) {
            model.addAttribute("error", "This email address has already been registered.");
            return "auth/register";
        }
        return "redirect:/login?registered";
    }
}